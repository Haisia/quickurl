package dev.haisia.quickurl.adapter.filter

import dev.haisia.quickurl.adapter.config.RateLimitProperties
import dev.haisia.quickurl.adapter.serializer.JsonSerializer
import dev.haisia.quickurl.adapter.webapi.dto.ApiErrorData
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.TimeUnit

/**
* Rate Limiting 필터
* IP 기준으로 요청 빈도를 제한합니다.
*/
@Component
class RateLimitFilter(
  private val redisTemplate: RedisTemplate<String, Any>,
  private val rateLimitProperties: RateLimitProperties,
  private val jsonSerializer: JsonSerializer,
) : OncePerRequestFilter() {

  companion object {
    private val log = LoggerFactory.getLogger(RateLimitFilter::class.java)
    private const val RATE_LIMIT_KEY_PREFIX = "rate_limit:"
  }

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {

    if (!rateLimitProperties.enabled || shouldExclude(request)) {
      filterChain.doFilter(request, response)
      return
    }

    val clientIp = getClientIp(request)

    try {
      val allowed = checkRateLimit(clientIp)
      val remaining = getRemainingRequests(clientIp)

      if (allowed) {
        // 요청 허용
        response.addHeader("X-RateLimit-Limit", rateLimitProperties.requestLimit.toString())
        response.addHeader("X-RateLimit-Remaining", remaining.toString())
        filterChain.doFilter(request, response)
      }

      // Rate Limit 초과
      val retryAfter = rateLimitProperties.refillDurationSeconds

      log.warn(
        "Rate limit exceeded for IP: {} on {} {} (retry after: {}s)",
        clientIp,
        request.method,
        request.requestURI,
        retryAfter
      )

      response.status = HttpStatus.TOO_MANY_REQUESTS.value()
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.addHeader("X-RateLimit-Limit", rateLimitProperties.requestLimit.toString())
      response.addHeader("X-RateLimit-Remaining", "0")
      response.addHeader("X-RateLimit-Retry-After", retryAfter.toString())

      response.writer.write(jsonSerializer.toJsonString(ApiErrorData("Rate limit exceeded. Please try again later.")))
      response.writer.flush()
    } catch (e: Exception) {
      log.error("Rate limit check failed for IP: {}", clientIp, e)
      // Rate Limit 체크 실패 시 요청 허용 (Fail Open)
      filterChain.doFilter(request, response)
    }
  }

  /**
  * Rate Limit 체크 (Token Bucket with Redis)
  */
  private fun checkRateLimit(clientIp: String): Boolean {
    val key = "$RATE_LIMIT_KEY_PREFIX$clientIp"
    val currentTime = System.currentTimeMillis()
    val windowKey = "$key:${currentTime / (rateLimitProperties.refillDurationSeconds * 1000)}"

    val currentCount = redisTemplate.opsForValue().increment(windowKey) ?: 1L

    // 첫 요청이면 만료 시간 설정
    if (currentCount == 1L) {
      redisTemplate.expire(
        windowKey,
        rateLimitProperties.refillDurationSeconds * 2,
        TimeUnit.SECONDS
      )
    }

    return currentCount <= rateLimitProperties.requestLimit
  }

  /**
  * 남은 요청 수 계산
  */
  private fun getRemainingRequests(clientIp: String): Long {
    val key = "$RATE_LIMIT_KEY_PREFIX$clientIp"
    val currentTime = System.currentTimeMillis()
    val windowKey = "$key:${currentTime / (rateLimitProperties.refillDurationSeconds * 1000)}"

    val currentCount = (redisTemplate.opsForValue().get(windowKey) as? String)?.toLongOrNull() ?: 0L
    val remaining = rateLimitProperties.requestLimit - currentCount

    return maxOf(0L, remaining)
  }

  /**
  * 클라이언트 IP 추출
  */
  private fun getClientIp(request: HttpServletRequest): String {
    val xForwardedFor = request.getHeader("X-Forwarded-For")

    return when {
      !xForwardedFor.isNullOrBlank() -> {
        // X-Forwarded-For의 첫 번째 IP (실제 클라이언트 IP)
        xForwardedFor.split(",").first().trim()
      }

      else -> {
        request.remoteAddr ?: "unknown"
      }
    }
  }

  /**
  * Rate Limit 제외 경로 확인
  */
  private fun shouldExclude(request: HttpServletRequest): Boolean {
    val path = request.requestURI
    return rateLimitProperties.excludedPaths.any { path.startsWith(it) }
  }
}

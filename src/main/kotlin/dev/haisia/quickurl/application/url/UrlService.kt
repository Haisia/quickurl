package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.`in`.UrlCreator
import dev.haisia.quickurl.application.out.UrlCacheRepository
import dev.haisia.quickurl.application.shared.out.AuthenticationContext
import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto
import dev.haisia.quickurl.application.url.`in`.UrlCleaner
import dev.haisia.quickurl.application.url.`in`.UrlFinder
import dev.haisia.quickurl.application.url.out.ClickStatsRepository
import dev.haisia.quickurl.application.url.out.UrlRepository
import dev.haisia.quickurl.application.user.UserNotFoundException
import dev.haisia.quickurl.application.user.out.UserRepository
import dev.haisia.quickurl.domain.url.Url
import dev.haisia.quickurl.domain.url.UrlEncoder
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class UrlService(
  private val urlRepository: UrlRepository,
  private val urlEncoder: UrlEncoder,
  private val urlCacheRepository: UrlCacheRepository,
  private val authenticationContext: AuthenticationContext,
  private val userRepository: UserRepository,
) : UrlCreator, UrlFinder, UrlCleaner {
  companion object {
    private val log = LoggerFactory.getLogger(UrlService::class.java)
  }

  override fun createShortKey(originalUrl: String): String {
    val shortKey = createOrGetShortKey(originalUrl)
    urlCacheRepository.set(shortKey, originalUrl)
    return shortKey
  }

  private fun createOrGetShortKey(originalUrl: String): String {
    val userIdOrNull = authenticationContext.getCurrentUserIdAllowNull()

    val url = urlRepository.findByOriginalUrlAndCreatedBy(originalUrl, userIdOrNull?.toString() ?: "anonymous")
      ?: urlRepository.save(Url.of(originalUrl = originalUrl, createdBy = userIdOrNull))

    if (!url.hasShortKey()) {
      url.generateShortKey(urlEncoder)
    }

    return url.getShortKeyOrThrow()
  }

  override fun findOriginalUrl(shortKey: String): String {
    val cachedUrl = urlCacheRepository.get(shortKey)
    if (cachedUrl != null) {
      return cachedUrl
    }

    val url = urlRepository.findByShortKey(shortKey) ?: throw ShortUrlNotFoundException("URL not found for key: $shortKey")
    url.click()

    urlCacheRepository.set(shortKey, url.originalUrl)

    return url.originalUrl
  }

  override fun findMyUrls(pageable: Pageable): Page<UrlWithClickCountDto> {
    val userId = authenticationContext.getCurrentUserId()
    val foundUser = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

    return urlRepository.findByCreatedByWithClickCount(foundUser.getIdOrThrow().toString(), pageable)
  }

  override fun deleteUnusedUrls(thresholdMonths: Long): Int {
    val threshold = LocalDateTime.now().minusMonths(thresholdMonths)
    log.info("Deleting URLs not used since: {}", threshold)
    
    val deletedCount = urlRepository.deleteByLastUsedAtBefore(threshold)
    
    log.info("Deleted {} unused URLs (threshold: {} months)", deletedCount, thresholdMonths)
    return deletedCount
  }

  override fun deleteMyUrl(shortKey: String) {
    val userId = authenticationContext.getCurrentUserId()

    urlRepository.deleteByShortKeyAndCreatedBy(shortKey, userId.toString()).let {
      if(it < 1) throw ShortUrlNotFoundException("URL not found for key: $shortKey")
      else log.info("Deleted URL for key: {}", shortKey)
    }
  }
}
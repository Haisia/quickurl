package dev.haisia.quickurl.adapter.filter

import dev.haisia.quickurl.adapter.security.CustomUserDetails
import dev.haisia.quickurl.application.shared.out.TokenResolver
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
  private val tokenResolver: TokenResolver
) : OncePerRequestFilter() {
  companion object {
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
  }

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    try {
      val token = extractToken(request)

      if (token != null && tokenResolver.validateToken(token)) {
        val userId = tokenResolver.getUserIdFromToken(token)
        val email = tokenResolver.getEmailFromToken(token)

        val userDetails = CustomUserDetails(
          userId = userId,
          email = email
        )

        val authentication = UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.authorities
        )

        SecurityContextHolder.getContext().authentication = authentication
      }
    } catch (e: Exception) {
      log.error("JWT authentication failed", e)
    }

    filterChain.doFilter(request, response)
  }

  private fun extractToken(request: HttpServletRequest): String? {
    val bearerToken = request.getHeader("Authorization")
    return if (bearerToken?.startsWith("Bearer ") == true) {
      bearerToken.substring(7)
    } else {
      null
    }
  }
}
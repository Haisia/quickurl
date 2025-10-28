package dev.haisia.quickurl.adapter.web.api

import dev.haisia.quickurl.adapter.config.CookieProperties
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CookieUtils(
  private val cookieProperties: CookieProperties
) {

  companion object {
    const val ACCESS_TOKEN_COOKIE_NAME = "accessToken"
    const val REFRESH_TOKEN_COOKIE_NAME = "refreshToken"
    const val REFRESH_TOKEN_COOKIE_PATH = "/api/v1/auth"
  }

  fun createAccessTokenCookie(token: String): ResponseCookie {
    return createCookie(
      name = ACCESS_TOKEN_COOKIE_NAME,
      value = token,
      maxAge = cookieProperties.accessTokenMaxAge.toLong()
    )
  }

  fun createRefreshTokenCookie(token: String): ResponseCookie {
    return createCookie(
      name = REFRESH_TOKEN_COOKIE_NAME,
      value = token,
      maxAge = cookieProperties.refreshTokenMaxAge.toLong(),
      path = REFRESH_TOKEN_COOKIE_PATH
    )
  }

  fun createExpiredAccessTokenCookie(): ResponseCookie {
    return createCookie(
      name = ACCESS_TOKEN_COOKIE_NAME,
      value = "",
      maxAge = 0
    )
  }

  fun createExpiredRefreshTokenCookie(): ResponseCookie {
    return createCookie(
      name = REFRESH_TOKEN_COOKIE_NAME,
      value = "",
      maxAge = 0,
      path = REFRESH_TOKEN_COOKIE_PATH
    )
  }

  private fun createCookie(name: String, value: String, maxAge: Long, path: String = "/"): ResponseCookie {
    val builder = ResponseCookie.from(name, value)
      .httpOnly(true)
      .secure(cookieProperties.secure)
      .path(path)
      .maxAge(Duration.ofSeconds(maxAge))
      .sameSite(cookieProperties.sameSite)

    // domain 설정 (null이 아닐 때만)
    cookieProperties.domain?.let { builder.domain(it) }

    return builder.build()
  }
}

package dev.haisia.quickurl.adapter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.cookie")
data class CookieProperties(
  var secure: Boolean = false,
  var domain: String? = null,
  var sameSite: String = "Strict",
  var accessTokenMaxAge: Int = 3600, // 1시간 (초 단위)
  var refreshTokenMaxAge: Int = 604800 // 7일 (초 단위)
)

package dev.haisia.quickurl.domain.url

import java.net.URI
import java.net.URISyntaxException

data class OriginalUrl(val value: String) {

  companion object {
    // 허용된 프로토콜 (http, https만 허용)
    private val ALLOWED_PROTOCOLS = setOf("http", "https")

    // 차단할 위험한 프로토콜
    private val BLOCKED_PROTOCOLS = setOf(
      "javascript", "data", "vbscript", "file", "about", "blob"
    )

    // XSS 패턴 감지
    private val XSS_PATTERNS = listOf(
      "<script",
      "javascript:",
      "onerror=",
      "onload=",
      "onclick=",
      "onmouseover=",
      "eval(",
      "expression("
    )

    private const val MAX_URL_LENGTH = 2048
  }

  init {
    validateUrl(value)
  }

  private fun validateUrl(url: String) {
    // 1. 빈 값 체크
    require(url.isNotBlank()) {
      throw InvalidOriginalUrlException("URL cannot be empty or blank")
    }

    // 2. 길이 체크
    require(url.length <= MAX_URL_LENGTH) {
      throw InvalidOriginalUrlException("URL is too long (max: $MAX_URL_LENGTH characters)")
    }

    // 3. XSS 패턴 체크
    val lowerUrl = url.lowercase()
    XSS_PATTERNS.forEach { pattern ->
      if (lowerUrl.contains(pattern)) {
        throw InvalidOriginalUrlException("URL contains potentially malicious content: $pattern")
      }
    }

    // 4. URI 형식 검증
    val uri = try {
      URI(url)
    } catch (e: URISyntaxException) {
      throw InvalidOriginalUrlException("Invalid URL format: ${e.message}")
    }

    // 5. 스키마(프로토콜) 검증
    val scheme = uri.scheme?.lowercase()

    require(scheme != null) {
      throw InvalidOriginalUrlException("URL must have a protocol (http or https)")
    }

    // 차단된 프로토콜 체크
    require(!BLOCKED_PROTOCOLS.contains(scheme)) {
      throw InvalidOriginalUrlException("Protocol '$scheme' is not allowed for security reasons")
    }

    // 허용된 프로토콜만 통과
    require(ALLOWED_PROTOCOLS.contains(scheme)) {
      throw InvalidOriginalUrlException("Only http and https protocols are allowed, but got: $scheme")
    }

    // 6. 호스트 검증
    val host = uri.host
    require(!host.isNullOrBlank()) {
      throw InvalidOriginalUrlException("URL must have a valid host")
    }
  }

  override fun toString(): String = value
}

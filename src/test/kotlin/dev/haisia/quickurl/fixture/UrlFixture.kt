package dev.haisia.quickurl.fixture

import dev.haisia.quickurl.domain.url.Url
import dev.haisia.quickurl.fixture.TestReflectionUtils.setFieldValue
import java.time.LocalDateTime

class UrlFixture {
  companion object {
    fun createUrl(
      id: Long? = null,
      shortKey: String? = null,
      originalUrl: String = "https://example.com",
      lastUsedAt: LocalDateTime? = null,
      createdAt: LocalDateTime? = null,
    ): Url {
      val url = Url.of(originalUrl = originalUrl)
      
      id?.let { url.setFieldValue("id", it) }
      shortKey?.let { url.setFieldValue("shortKey", it) }
      createdAt?.let { url.setFieldValue("createdAt", it) }
      lastUsedAt?.let { url.setFieldValue("lastUsedAt", it) }
      
      return url
    }
  }
}

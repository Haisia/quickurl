package dev.haisia.quickurl.fixture

import dev.haisia.quickurl.domain.Url
import dev.haisia.quickurl.fixture.TestReflectionUtils.setFieldValue
import java.time.LocalDateTime

class UrlFixture {
  companion object {
    fun createUrl(
      id: Long? = null,
      shortKey: String = "abc123",
      originalUrl: String = "https://example.com",
      lastUsedAt: LocalDateTime? = null,
      createdAt: LocalDateTime? = null,
      isDeleted: Boolean? = null,
    ): Url {
      val url = Url.of(shortKey = shortKey, originalUrl = originalUrl)
      
      id?.let { url.setFieldValue("id", it) }
      createdAt?.let { url.setFieldValue("createdAt", it) }
      isDeleted?.let { url.setFieldValue("isDeleted", it) }
      lastUsedAt?.let { url.setFieldValue("lastUsedAt", it) }
      
      return url
    }
  }
}

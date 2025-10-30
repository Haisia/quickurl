package dev.haisia.quickurl.fixture

import dev.haisia.quickurl.domain.Duration
import dev.haisia.quickurl.domain.url.OriginalUrl
import dev.haisia.quickurl.domain.url.Url
import dev.haisia.quickurl.domain.url.UrlClickLog
import dev.haisia.quickurl.fixture.TestReflectionUtils.setFieldValue
import java.time.LocalDateTime
import java.util.*

class UrlFixture {
  companion object {
    fun createUrl(
      id: Long? = null,
      shortKey: String? = null,
      originalUrl: OriginalUrl = OriginalUrl("https://example.com"),
      createdBy: UUID? = null,
      lastUsedAt: LocalDateTime? = null,
      expiresAt: LocalDateTime? = null,
      createdAt: LocalDateTime? = null,
      expirationDuration: Duration = Duration.NONE,
    ): Url {
      val url = Url.of(
        originalUrl = originalUrl,
        expirationDuration = expirationDuration,
        createdBy = createdBy,
      )

      id?.let { url.setFieldValue("id", it) }
      shortKey?.let { url.setFieldValue("shortKey", it) }
      lastUsedAt?.let { url.setFieldValue("lastUsedAt", it) }
      expiresAt?.let { url.setFieldValue("expiresAt", it) }
      createdAt?.let { url.setFieldValue("createdAt", it) }

      return url
    }
    
    fun createUrlClickLog(
      id: Long? = null,
      shortKey: String = "abcd123",
      ipAddress: String? = null,
      userAgent: String? = null,
      referer: String? = null,
      clickedAt: LocalDateTime = LocalDateTime.now(),
    ): UrlClickLog{
      val urlClickLog = UrlClickLog.of(
        shortKey = shortKey,
        ipAddress = ipAddress,
        userAgent = userAgent,
        referer = referer,
      )
      
      id?.let { urlClickLog.setFieldValue("id", it) }
      shortKey.let { urlClickLog.setFieldValue("shortKey", it) }
      ipAddress?.let { urlClickLog.setFieldValue("ipAddress", it) }
      userAgent?.let { urlClickLog.setFieldValue("userAgent", it) }
      referer?.let { urlClickLog.setFieldValue("referer", it) }
      clickedAt.let { urlClickLog.setFieldValue("clickedAt", it) }
      
      return urlClickLog
    }
  }
}

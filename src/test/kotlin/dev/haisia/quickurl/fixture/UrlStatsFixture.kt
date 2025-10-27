package dev.haisia.quickurl.fixture

import dev.haisia.quickurl.domain.Url
import dev.haisia.quickurl.domain.UrlStats
import dev.haisia.quickurl.fixture.TestReflectionUtils.setFieldValue
import java.time.LocalDateTime

class UrlStatsFixture {
  companion object {
    fun createUrlStats(
      id: Long? = null,
      ipAddress: String = "192.168.0.1",
      country: String = "South Korea",
      device: String = "Mobile",
      browser: String = "Chrome",
      url: Url = UrlFixture.createUrl(),
      createdAt: LocalDateTime? = null,
    ): UrlStats {
      val urlStats = UrlStats.of(
        ipAddress = ipAddress,
        country = country,
        device = device,
        browser = browser,
        url = url
      )
      
      id?.let { urlStats.setFieldValue("id", it) }
      createdAt?.let { urlStats.setFieldValue("createdAt", it) }
      
      return urlStats
    }
  }
}

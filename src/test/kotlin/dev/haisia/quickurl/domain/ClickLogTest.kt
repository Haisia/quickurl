package dev.haisia.quickurl.domain

import dev.haisia.quickurl.domain.url.UrlClickLog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ClickLogTest {

  @Test
  @DisplayName("ClickLog를 생성한다")
  fun testCreateClickLog() {
    val shortKey = "abc123"
    val ipAddress = "192.168.1.1"
    val userAgent = "Mozilla/5.0"
    val referer = "https://example.com"

    val urlClickLog = UrlClickLog.of(shortKey, ipAddress, userAgent, referer)

    assertEquals(shortKey, urlClickLog.shortKey)
    assertEquals(ipAddress, urlClickLog.ipAddress)
    assertEquals(userAgent, urlClickLog.userAgent)
    assertEquals(referer, urlClickLog.referer)
    assertNotNull(urlClickLog.clickedAt)
  }

  @Test
  @DisplayName("nullable 필드로 ClickLog를 생성한다")
  fun testCreateClickLogWithNullableFields() {
    val shortKey = "abc123"

    val urlClickLog = UrlClickLog.of(shortKey, null, null, null)

    assertEquals(shortKey, urlClickLog.shortKey)
    assertEquals(null, urlClickLog.ipAddress)
    assertEquals(null, urlClickLog.userAgent)
    assertEquals(null, urlClickLog.referer)
    assertNotNull(urlClickLog.clickedAt)
  }
}

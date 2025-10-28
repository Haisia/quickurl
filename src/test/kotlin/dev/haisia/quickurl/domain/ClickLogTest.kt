package dev.haisia.quickurl.domain

import dev.haisia.quickurl.domain.url.ClickLog
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

    val clickLog = ClickLog.of(shortKey, ipAddress, userAgent, referer)

    assertEquals(shortKey, clickLog.shortKey)
    assertEquals(ipAddress, clickLog.ipAddress)
    assertEquals(userAgent, clickLog.userAgent)
    assertEquals(referer, clickLog.referer)
    assertNotNull(clickLog.clickedAt)
  }

  @Test
  @DisplayName("nullable 필드로 ClickLog를 생성한다")
  fun testCreateClickLogWithNullableFields() {
    val shortKey = "abc123"

    val clickLog = ClickLog.of(shortKey, null, null, null)

    assertEquals(shortKey, clickLog.shortKey)
    assertEquals(null, clickLog.ipAddress)
    assertEquals(null, clickLog.userAgent)
    assertEquals(null, clickLog.referer)
    assertNotNull(clickLog.clickedAt)
  }
}

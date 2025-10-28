package dev.haisia.quickurl.application

import dev.haisia.quickurl.application.out.ClickLogRepository
import dev.haisia.quickurl.application.url.UrlClickLogService
import dev.haisia.quickurl.domain.url.UrlClickLog
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ClickLogServiceTest {

  private val clickLogRepository = mockk<ClickLogRepository>()
  private val urlClickLogService = UrlClickLogService(clickLogRepository)

  @Test
  @DisplayName("클릭 로그를 성공적으로 저장한다")
  fun testLogClickAsync() {
    val shortKey = "abc123"
    val ipAddress = "192.168.1.1"
    val userAgent = "Mozilla/5.0"
    val referer = "https://example.com"

    val urlClickLogSlot = slot<UrlClickLog>()
    every { clickLogRepository.save(capture(urlClickLogSlot)) } answers { firstArg() }

    urlClickLogService.logClickAsync(shortKey, ipAddress, userAgent, referer)

    verify { clickLogRepository.save(any()) }
    
    val savedLog = urlClickLogSlot.captured
    assertEquals(shortKey, savedLog.shortKey)
    assertEquals(ipAddress, savedLog.ipAddress)
    assertEquals(userAgent, savedLog.userAgent)
    assertEquals(referer, savedLog.referer)
    assertNotNull(savedLog.clickedAt)
  }

  @Test
  @DisplayName("클릭 수를 조회한다")
  fun testGetClickCount() {
    val shortKey = "abc123"
    val expectedCount = 42L
    every { clickLogRepository.countByShortKey(shortKey) } returns expectedCount

    val result = urlClickLogService.getClickCount(shortKey)

    assertEquals(expectedCount, result)
    verify { clickLogRepository.countByShortKey(shortKey) }
  }
}

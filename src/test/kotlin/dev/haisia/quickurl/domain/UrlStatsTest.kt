package dev.haisia.quickurl.domain

import dev.haisia.quickurl.fixture.UrlFixture
import dev.haisia.quickurl.fixture.UrlStatsFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("UrlStats 엔티티 테스트")
class UrlStatsTest {

  @Test
  @DisplayName("팩토리 메서드로 UrlStats 객체를 생성할 수 있다")
  fun testCreateUrlStatsWithFactoryMethod() {
    val ipAddress = "192.168.0.1"
    val country = "South Korea"
    val device = "Mobile"
    val browser = "Chrome"
    val url = Url.of("abc123", "https://example.com")

    val urlStats = UrlStats.of(ipAddress, country, device, browser, url)

    assertNotNull(urlStats)
    assertEquals(ipAddress, urlStats.ipAddress)
    assertEquals(country, urlStats.country)
    assertEquals(device, urlStats.device)
    assertEquals(browser, urlStats.browser)
    assertSame(url, urlStats.url)
  }

  @Test
  @DisplayName("생성 시 createdAt 기본값이 설정된다")
  fun testDefaultCreatedAt() {
    val url = Url.of("abc123", "https://example.com")
    val beforeCreation = LocalDateTime.now().minusSeconds(1)

    val urlStats = UrlStats.of("192.168.0.1", "Korea", "Desktop", "Firefox", url)
    val afterCreation = LocalDateTime.now().plusSeconds(1)

    assertNotNull(urlStats.createdAt)
    assertTrue(urlStats.createdAt.isAfter(beforeCreation) && urlStats.createdAt.isBefore(afterCreation))
  }

  @Test
  @DisplayName("Url과의 연관관계가 올바르게 설정된다")
  fun testUrlRelationship() {
    val url = Url.of("test123", "https://test.example.com")

    val urlStats = UrlStatsFixture.createUrlStats(url = url)

    assertNotNull(urlStats.url)
    assertEquals(url, urlStats.url)
    assertEquals("test123", urlStats.url.shortKey)
    assertEquals("https://test.example.com", urlStats.url.originalUrl)
  }

  @Test
  @DisplayName("같은 ID를 가진 UrlStats 객체는 동등하다")
  fun testEqualsWithSameId() {
    val url = UrlFixture.createUrl()
    val urlStats1 = UrlStatsFixture.createUrlStats(
      id = 1L,
      ipAddress = "192.168.0.1",
      country = "Korea",
      device = "Mobile",
      browser = "Chrome",
      url = url
    )
    val urlStats2 = UrlStatsFixture.createUrlStats(
      id = 1L,
      ipAddress = "10.0.0.1",
      country = "USA",
      device = "Desktop",
      browser = "Firefox",
      url = url
    )

    // when & then
    assertEquals(urlStats1, urlStats2)
  }

  @Test
  @DisplayName("다른 ID를 가진 UrlStats 객체는 동등하지 않다")
  fun testEqualsWithDifferentId() {
    val url = UrlFixture.createUrl()
    val urlStats1 = UrlStatsFixture.createUrlStats(id = 1L, url = url)
    val urlStats2 = UrlStatsFixture.createUrlStats(id = 2L, url = url)

    assertNotEquals(urlStats1, urlStats2)
  }

  @Test
  @DisplayName("ID가 null인 UrlStats 객체는 동등성 비교 시 ID로 비교하지 않는다")
  fun testEqualsWithNullId() {
    val url = UrlFixture.createUrl()
    val urlStats1 = UrlStatsFixture.createUrlStats(url = url)
    val urlStats2 = UrlStatsFixture.createUrlStats(url = url)

    assertNotEquals(urlStats1, urlStats2)
  }

  @Test
  @DisplayName("hashCode는 클래스 타입에 기반한다")
  fun testHashCode() {
    val url = UrlFixture.createUrl()
    val urlStats1 = UrlStatsFixture.createUrlStats(
      ipAddress = "192.168.0.1",
      country = "Korea",
      url = url
    )
    val urlStats2 = UrlStatsFixture.createUrlStats(
      ipAddress = "10.0.0.1",
      country = "USA",
      url = url
    )

    assertEquals(urlStats1.hashCode(), urlStats2.hashCode())
  }

  @Test
  @DisplayName("모든 필드가 올바르게 저장된다")
  fun testAllFields() {
    val ipAddress = "172.16.0.1"
    val country = "Japan"
    val device = "Desktop"
    val browser = "Edge"
    val url = UrlFixture.createUrl(shortKey = "xyz789", originalUrl = "https://example.org")

    val urlStats = UrlStatsFixture.createUrlStats(
      ipAddress = ipAddress,
      country = country,
      device = device,
      browser = browser,
      url = url
    )

    assertEquals(ipAddress, urlStats.ipAddress)
    assertEquals(country, urlStats.country)
    assertEquals(device, urlStats.device)
    assertEquals(browser, urlStats.browser)
    assertEquals(url, urlStats.url)
    assertNotNull(urlStats.createdAt)
  }

  @Test
  @DisplayName("다른 Url을 참조하는 UrlStats를 생성할 수 있다")
  fun testDifferentUrlReferences() {
    val url1 = UrlFixture.createUrl(shortKey = "short1", originalUrl = "https://example1.com")
    val url2 = UrlFixture.createUrl(shortKey = "short2", originalUrl = "https://example2.com")

    val urlStats1 = UrlStatsFixture.createUrlStats(url = url1)
    val urlStats2 = UrlStatsFixture.createUrlStats(url = url2)

    assertNotEquals(urlStats1.url, urlStats2.url)
    assertEquals("short1", urlStats1.url.shortKey)
    assertEquals("short2", urlStats2.url.shortKey)
  }

  @Test
  @DisplayName("특정 시간으로 생성일시를 설정할 수 있다")
  fun testCustomCreatedAt() {
    val customTime = LocalDateTime.of(2024, 6, 15, 10, 30)
    val url = UrlFixture.createUrl()

    val urlStats = UrlStatsFixture.createUrlStats(createdAt = customTime, url = url)

    assertEquals(customTime, urlStats.createdAt)
  }
}

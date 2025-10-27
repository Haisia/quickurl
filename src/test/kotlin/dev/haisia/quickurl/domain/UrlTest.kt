package dev.haisia.quickurl.domain

import dev.haisia.quickurl.fixture.UrlFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("Url 엔티티 테스트")
class UrlTest {

  @Test
  @DisplayName("팩토리 메서드로 Url 객체를 생성할 수 있다")
  fun testCreateUrlWithFactoryMethod() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"

    val url = Url.of(shortKey, originalUrl)

    assertNotNull(url)
    assertEquals(shortKey, url.shortKey)
    assertEquals(originalUrl, url.originalUrl)
  }

  @Test
  @DisplayName("생성 시 기본값이 올바르게 설정된다")
  fun testDefaultValues() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"
    val beforeCreation = LocalDateTime.now().minusSeconds(1)

    val url = Url.of(shortKey, originalUrl)
    val afterCreation = LocalDateTime.now().plusSeconds(1)

    assertNotNull(url.lastUsedAt)
    assertNotNull(url.createdAt)
    assertFalse(url.isDeleted)
    assertTrue(url.lastUsedAt.isAfter(beforeCreation) && url.lastUsedAt.isBefore(afterCreation))
    assertTrue(url.createdAt.isAfter(beforeCreation) && url.createdAt.isBefore(afterCreation))
  }

  @Test
  @DisplayName("같은 ID를 가진 Url 객체는 동등하다")
  fun testEqualsWithSameId() {
    val url1 = UrlFixture.createUrl(id = 1L, shortKey = "abc123", originalUrl = "https://example.com")
    val url2 = UrlFixture.createUrl(id = 1L, shortKey = "def456", originalUrl = "https://different.com")

    assertEquals(url1, url2)
  }

  @Test
  @DisplayName("다른 ID를 가진 Url 객체는 동등하지 않다")
  fun testEqualsWithDifferentId() {
    val url1 = UrlFixture.createUrl(id = 1L, shortKey = "abc123", originalUrl = "https://example.com")
    val url2 = UrlFixture.createUrl(id = 2L, shortKey = "abc123", originalUrl = "https://example.com")

    assertNotEquals(url1, url2)
  }

  @Test
  @DisplayName("ID가 null인 Url 객체는 동등성 비교 시 ID로 비교하지 않는다")
  fun testEqualsWithNullId() {
    val url1 = UrlFixture.createUrl(shortKey = "abc123", originalUrl = "https://example.com")
    val url2 = UrlFixture.createUrl(shortKey = "abc123", originalUrl = "https://example.com")

    assertNotEquals(url1, url2)
  }

  @Test
  @DisplayName("hashCode는 클래스 타입에 기반한다")
  fun testHashCode() {
    val url1 = UrlFixture.createUrl(shortKey = "abc123", originalUrl = "https://example.com")
    val url2 = UrlFixture.createUrl(shortKey = "def456", originalUrl = "https://different.com")

    assertEquals(url1.hashCode(), url2.hashCode())
  }

  @Test
  @DisplayName("shortKey와 originalUrl이 올바르게 저장된다")
  fun testUrlFields() {
    val shortKey = "test123"
    val originalUrl = "https://test.example.com/path?query=value"

    val url = UrlFixture.createUrl(shortKey = shortKey, originalUrl = originalUrl)

    assertEquals(shortKey, url.shortKey)
    assertEquals(originalUrl, url.originalUrl)
  }

  @Test
  @DisplayName("특정 시간으로 생성일시를 설정할 수 있다")
  fun testCustomCreatedAt() {
    val customTime = LocalDateTime.of(2024, 1, 1, 12, 0)

    val url = UrlFixture.createUrl(createdAt = customTime)

    assertEquals(customTime, url.createdAt)
  }

  @Test
  @DisplayName("삭제 상태를 설정할 수 있다")
  fun testIsDeleted() {
    val deletedUrl = UrlFixture.createUrl(isDeleted = true)
    val activeUrl = UrlFixture.createUrl(isDeleted = false)

    assertTrue(deletedUrl.isDeleted)
    assertFalse(activeUrl.isDeleted)
  }
}

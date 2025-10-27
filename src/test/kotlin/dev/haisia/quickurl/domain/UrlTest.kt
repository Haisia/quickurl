package dev.haisia.quickurl.domain

import dev.haisia.quickurl.fixture.UrlFixture
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@DisplayName("Url 엔티티 테스트")
class UrlTest {

  @Test
  @DisplayName("팩토리 메서드로 Url 객체를 생성할 수 있다")
  fun testCreateUrlWithFactoryMethod() {
    val originalUrl = "https://example.com"

    val url = Url.of(originalUrl)

    assertNotNull(url)
    assertEquals(originalUrl, url.originalUrl)
  }

  @Test
  @DisplayName("생성 시 기본값이 올바르게 설정된다")
  fun testDefaultValues() {
    val originalUrl = "https://example.com"
    val beforeCreation = LocalDateTime.now().minusSeconds(1)

    val url = Url.of(originalUrl)
    val afterCreation = LocalDateTime.now().plusSeconds(1)

    assertNotNull(url.lastUsedAt)
    assertNotNull(url.createdAt)
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
  @DisplayName("originalUrl이 올바르게 저장된다")
  fun testUrlFields() {
    val originalUrl = "https://test.example.com/path?query=value"

    val url = UrlFixture.createUrl(originalUrl = originalUrl)

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
  @DisplayName("shortKey 생성 전에는 hasShortKey가 false를 반환한다")
  fun testHasShortKeyBeforeGeneration() {
    val url = Url.of("https://example.com")

    assertFalse(url.hasShortKey())
  }

  @Test
  @DisplayName("shortKey가 설정되면 hasShortKey가 true를 반환한다")
  fun testHasShortKeyAfterSetting() {
    val url = UrlFixture.createUrl(shortKey = "abc123")

    assertTrue(url.hasShortKey())
  }

  @Test
  @DisplayName("ID가 있으면 UrlEncoder로 shortKey를 생성할 수 있다")
  fun testGenerateShortKey() {
    val url = UrlFixture.createUrl(id = 123L, originalUrl = "https://example.com")
    val mockEncoder = object : UrlEncoder {
      override fun encode(id: Long): String = "encoded_${id}"
      override fun decode(url: String): Long = 0L
    }

    url.generateShortKey(mockEncoder)

    assertTrue(url.hasShortKey())
    assertEquals("encoded_123", url.shortKey)
  }

  @Test
  @DisplayName("ID가 null이면 shortKey 생성 시 예외가 발생한다")
  fun testGenerateShortKeyWithNullId() {
    val url = Url.of("https://example.com")
    val mockEncoder = object : UrlEncoder {
      override fun encode(id: Long): String = "encoded_${id}"
      override fun decode(url: String): Long = 0L
    }

    val exception = assertThrows(ShortKeyGenerationException::class.java) {
      url.generateShortKey(mockEncoder)
    }
    
    assertTrue(exception.message?.contains("Id must not be null") ?: false)
  }

  @Test
  @DisplayName("shortKey가 있으면 requireShortKey가 값을 반환한다")
  fun testRequireShortKeyWithValue() {
    val url = UrlFixture.createUrl(shortKey = "abc123")

    val shortKey = url.requireShortKey()

    assertEquals("abc123", shortKey)
  }

  @Test
  @DisplayName("shortKey가 없으면 requireShortKey가 예외를 발생시킨다")
  fun testRequireShortKeyWithoutValue() {
    val url = Url.of("https://example.com")

    val exception = assertThrows(ShortKeyNotGeneratedException::class.java) {
      url.requireShortKey()
    }
    
    assertTrue(exception.message?.contains("Short key has not been generated yet") ?: false)
  }

  @Test
  @DisplayName("lastUsedAt 값을 업데이트할 수 있다")
  fun testUpdateLastUsedAt() {
    val customTime = LocalDateTime.of(2024, 6, 15, 10, 30)
    val url = UrlFixture.createUrl()

    url.lastUsedAt = customTime

    assertEquals(customTime, url.lastUsedAt)
  }
}

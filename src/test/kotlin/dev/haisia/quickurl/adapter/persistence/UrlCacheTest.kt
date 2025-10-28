package dev.haisia.quickurl.adapter.persistence

import dev.haisia.quickurl.adapter.persistence.url.UrlCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("UrlCache 엔티티 테스트")
class UrlCacheTest {

  @Test
  @DisplayName("UrlCache 객체를 생성할 수 있다")
  fun createUrlCache() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"

    val urlCache = UrlCache(shortKey = shortKey, originalUrl = originalUrl)

    assertEquals(shortKey, urlCache.shortKey)
    assertEquals(originalUrl, urlCache.originalUrl)
  }

  @Test
  @DisplayName("기본 TTL은 24시간이다")
  fun defaultTtl() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"

    val urlCache = UrlCache(shortKey = shortKey, originalUrl = originalUrl)

    assertEquals(24L, urlCache.ttl)
  }

  @Test
  @DisplayName("커스텀 TTL을 설정할 수 있다")
  fun customTtl() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"
    val customTtl = 48L

    val urlCache = UrlCache(shortKey = shortKey, originalUrl = originalUrl, ttl = customTtl)

    assertEquals(customTtl, urlCache.ttl)
  }

  @Test
  @DisplayName("같은 내용의 UrlCache 객체는 동등하다")
  fun equality() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"

    val urlCache1 = UrlCache(shortKey = shortKey, originalUrl = originalUrl)
    val urlCache2 = UrlCache(shortKey = shortKey, originalUrl = originalUrl)

    assertEquals(urlCache1, urlCache2)
  }

  @Test
  @DisplayName("copy를 사용하여 객체를 복사할 수 있다")
  fun copyUrlCache() {
    val original = UrlCache(
      shortKey = "abc123",
      originalUrl = "https://example.com",
      ttl = 12L
    )

    val copied = original.copy(originalUrl = "https://different.com")

    assertEquals(original.shortKey, copied.shortKey)
    assertEquals("https://different.com", copied.originalUrl)
    assertEquals(original.ttl, copied.ttl)
  }
}

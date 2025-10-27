package dev.haisia.quickurl.adapter.persistence

import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@DisplayName("UrlCacheRepositoryImpl 테스트")
class UrlCacheRepositoryImplTest {

  private lateinit var urlCacheRedisRepository: UrlCacheRedisRepository
  private lateinit var urlCacheRepositoryImpl: UrlCacheRepositoryImpl

  @BeforeEach
  fun setUp() {
    urlCacheRedisRepository = mockk()
    urlCacheRepositoryImpl = UrlCacheRepositoryImpl(urlCacheRedisRepository)
  }

  @Test
  @DisplayName("shortKey로 캐시를 조회할 수 있다")
  fun getFromCache() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"
    val urlCache = UrlCache(shortKey = shortKey, originalUrl = originalUrl)

    every { urlCacheRedisRepository.findById(shortKey) } returns Optional.of(urlCache)

    val result = urlCacheRepositoryImpl.get(shortKey)

    assertEquals(originalUrl, result)
    verify { urlCacheRedisRepository.findById(shortKey) }
  }

  @Test
  @DisplayName("존재하지 않는 shortKey로 조회하면 null을 반환한다")
  fun getFromCacheNotFound() {
    val shortKey = "nonexistent"

    every { urlCacheRedisRepository.findById(shortKey) } returns Optional.empty()

    val result = urlCacheRepositoryImpl.get(shortKey)

    assertNull(result)
    verify { urlCacheRedisRepository.findById(shortKey) }
  }

  @Test
  @DisplayName("캐시에 데이터를 저장할 수 있다")
  fun setToCache() {
    val shortKey = "abc123"
    val originalUrl = "https://example.com"
    val urlCache = UrlCache(shortKey = shortKey, originalUrl = originalUrl)

    every { urlCacheRedisRepository.save(any()) } returns urlCache

    urlCacheRepositoryImpl.set(shortKey, originalUrl)

    verify { urlCacheRedisRepository.save(match {
      it.shortKey == shortKey && it.originalUrl == originalUrl 
    }) }
  }

  @Test
  @DisplayName("캐시에서 데이터를 삭제할 수 있다")
  fun deleteFromCache() {
    val shortKey = "abc123"

    every { urlCacheRedisRepository.deleteById(shortKey) } just Runs

    urlCacheRepositoryImpl.delete(shortKey)

    verify { urlCacheRedisRepository.deleteById(shortKey) }
  }
}

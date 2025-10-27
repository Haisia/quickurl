package dev.haisia.quickurl.application

import dev.haisia.quickurl.application.out.UrlCacheRepository
import dev.haisia.quickurl.application.out.UrlRepository
import dev.haisia.quickurl.domain.Url
import dev.haisia.quickurl.domain.UrlEncoder
import dev.haisia.quickurl.fixture.UrlFixture
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("UrlService 테스트")
class UrlServiceTest {

  private lateinit var urlRepository: UrlRepository
  private lateinit var urlEncoder: UrlEncoder
  private lateinit var urlCacheRepository: UrlCacheRepository
  private lateinit var urlService: UrlService

  @BeforeEach
  fun setUp() {
    urlRepository = mockk()
    urlEncoder = mockk()
    urlCacheRepository = mockk()
    urlService = UrlService(urlRepository, urlEncoder, urlCacheRepository)
  }

  @Nested
  @DisplayName("createShortUrl 메서드는")
  inner class CreateShortUrlTest {

    @Test
    @DisplayName("새로운 URL을 생성하고 shortKey를 생성한 후 캐시에 저장한다")
    fun createNewUrlAndGenerateShortKey() {
      val originalUrl = "https://example.com"
      val savedUrl = UrlFixture.createUrl(id = 1L, originalUrl = originalUrl)
      val expectedShortKey = "abc123"

      every { urlRepository.findByOriginalUrl(originalUrl) } returns null
      every { urlRepository.save(any()) } returns savedUrl
      every { urlEncoder.encode(1L) } returns expectedShortKey
      every { urlCacheRepository.set(expectedShortKey, originalUrl) } just Runs

      val result = urlService.createShortUrl(originalUrl)

      assertEquals(expectedShortKey, result)
      verify { urlRepository.findByOriginalUrl(originalUrl) }
      verify { urlRepository.save(any()) }
      verify { urlEncoder.encode(1L) }
      verify { urlCacheRepository.set(expectedShortKey, originalUrl) }
    }

    @Test
    @DisplayName("이미 존재하는 URL이 shortKey가 없으면 새로 생성하고 캐시에 저장한다")
    fun generateShortKeyForExistingUrlWithoutShortKey() {
      val originalUrl = "https://example.com"
      val existingUrl = UrlFixture.createUrl(id = 1L, originalUrl = originalUrl)
      val expectedShortKey = "abc123"

      every { urlRepository.findByOriginalUrl(originalUrl) } returns existingUrl
      every { urlEncoder.encode(1L) } returns expectedShortKey
      every { urlCacheRepository.set(expectedShortKey, originalUrl) } just Runs

      val result = urlService.createShortUrl(originalUrl)

      assertEquals(expectedShortKey, result)
      verify { urlRepository.findByOriginalUrl(originalUrl) }
      verify(exactly = 0) { urlRepository.save(any()) }
      verify { urlEncoder.encode(1L) }
      verify { urlCacheRepository.set(expectedShortKey, originalUrl) }
    }

    @Test
    @DisplayName("이미 존재하는 URL이 shortKey가 있으면 기존 shortKey를 반환하고 캐시에 저장한다")
    fun returnExistingShortKey() {
      val originalUrl = "https://example.com"
      val existingShortKey = "existing123"
      val existingUrl = UrlFixture.createUrl(
        id = 1L,
        shortKey = existingShortKey,
        originalUrl = originalUrl
      )

      every { urlRepository.findByOriginalUrl(originalUrl) } returns existingUrl
      every { urlCacheRepository.set(existingShortKey, originalUrl) } just Runs

      val result = urlService.createShortUrl(originalUrl)

      assertEquals(existingShortKey, result)
      verify { urlRepository.findByOriginalUrl(originalUrl) }
      verify(exactly = 0) { urlRepository.save(any()) }
      verify(exactly = 0) { urlEncoder.encode(any()) }
      verify { urlCacheRepository.set(existingShortKey, originalUrl) }
    }
  }

  @Nested
  @DisplayName("findOriginalUrl 메서드는")
  inner class FindOriginalUrlTest {

    @Test
    @DisplayName("캐시에 있는 경우 캐시에서 조회한다")
    fun findFromCache() {
      val shortKey = "abc123"
      val originalUrl = "https://example.com"

      every { urlCacheRepository.get(shortKey) } returns originalUrl

      val result = urlService.findOriginalUrl(shortKey)

      assertEquals(originalUrl, result)
      verify { urlCacheRepository.get(shortKey) }
      verify(exactly = 0) { urlRepository.findByShortKey(any()) }
    }

    @Test
    @DisplayName("캐시에 없는 경우 DB에서 조회하고 캐시에 저장한다")
    fun findFromDatabaseAndSaveToCache() {
      val shortKey = "abc123"
      val originalUrl = "https://example.com"
      val url = UrlFixture.createUrl(id = 1L, shortKey = shortKey, originalUrl = originalUrl)

      every { urlCacheRepository.get(shortKey) } returns null
      every { urlRepository.findByShortKey(shortKey) } returns url
      every { urlCacheRepository.set(shortKey, originalUrl) } just Runs

      val result = urlService.findOriginalUrl(shortKey)

      assertEquals(originalUrl, result)
      verify { urlCacheRepository.get(shortKey) }
      verify { urlRepository.findByShortKey(shortKey) }
      verify { urlCacheRepository.set(shortKey, originalUrl) }
    }

    @Test
    @DisplayName("캐시와 DB 모두에 없는 경우 예외를 발생시킨다")
    fun throwExceptionWhenNotFound() {
      val shortKey = "nonexistent"

      every { urlCacheRepository.get(shortKey) } returns null
      every { urlRepository.findByShortKey(shortKey) } returns null

      val exception = assertThrows(IllegalArgumentException::class.java) {
        urlService.findOriginalUrl(shortKey)
      }

      assertEquals("URL not found for key: $shortKey", exception.message)
      verify { urlCacheRepository.get(shortKey) }
      verify { urlRepository.findByShortKey(shortKey) }
      verify(exactly = 0) { urlCacheRepository.set(any(), any()) }
    }
  }
}

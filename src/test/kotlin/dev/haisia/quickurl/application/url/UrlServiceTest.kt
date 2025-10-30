package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.shared.out.AuthenticationContext
import dev.haisia.quickurl.application.url.dto.UrlClickDto
import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto
import dev.haisia.quickurl.application.url.out.UrlCacheRepository
import dev.haisia.quickurl.application.url.out.UrlRepository
import dev.haisia.quickurl.application.user.UserNotFoundException
import dev.haisia.quickurl.application.user.out.UserRepository
import dev.haisia.quickurl.domain.Duration
import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.url.OriginalUrl
import dev.haisia.quickurl.domain.url.UrlEncoder
import dev.haisia.quickurl.fixture.UrlFixture
import dev.haisia.quickurl.fixture.UserFixture
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.*

@DisplayName("UrlService 테스트")
class UrlServiceTest {
  
  private val urlRepository: UrlRepository = mockk()
  private val urlEncoder: UrlEncoder = mockk()
  private val urlCacheRepository: UrlCacheRepository = mockk()
  private val authenticationContext: AuthenticationContext = mockk()
  private val userRepository: UserRepository = mockk()
  private val eventPublisher: ApplicationEventPublisher = mockk(relaxed = true)
  
  private lateinit var urlService: UrlService
  
  @BeforeEach
  fun setUp() {
    urlService = UrlService(
      urlRepository = urlRepository,
      urlEncoder = urlEncoder,
      urlCacheRepository = urlCacheRepository,
      authenticationContext = authenticationContext,
      userRepository = userRepository,
      eventPublisher = eventPublisher
    )
  }
  
  @Test
  @DisplayName("익명 사용자가 Short Key를 생성할 수 있다")
  fun createShortKeyAsAnonymousUser() {
    val originalUrl = OriginalUrl("https://example.com")
    val url = UrlFixture.createUrl(id = 1L, originalUrl = originalUrl, shortKey = null)
    val shortKey = "abc123"
    
    every { authenticationContext.getCurrentUserIdAllowNull() } returns null
    every { urlRepository.findByOriginalUrlAndCreatedBy(originalUrl, "anonymous") } returns null
    every { urlRepository.save(any()) } returns url
    every { urlEncoder.encode(1L) } returns shortKey
    every { urlCacheRepository.set(shortKey, originalUrl) } just Runs
    
    val result = urlService.createShortKey(originalUrl, Duration.NONE)
    
    assert(result == shortKey)
    verify { urlRepository.save(any()) }
    verify { urlCacheRepository.set(shortKey, originalUrl) }
    verify { eventPublisher.publishEvent(any<UrlEvent.UrlCreated>()) }
  }
  
  @Test
  @DisplayName("인증된 사용자가 Short Key를 생성할 수 있다")
  fun createShortKeyAsAuthenticatedUser() {
    val originalUrl = OriginalUrl("https://example.com")
    val userId = UUID.randomUUID()
    val url = UrlFixture.createUrl(id = 1L, originalUrl = originalUrl, shortKey = null, createdBy = userId)
    val shortKey = "abc123"
    
    every { authenticationContext.getCurrentUserIdAllowNull() } returns userId
    every { urlRepository.findByOriginalUrlAndCreatedBy(originalUrl, userId.toString()) } returns null
    every { urlRepository.save(any()) } returns url
    every { urlEncoder.encode(1L) } returns shortKey
    every { urlCacheRepository.set(shortKey, originalUrl) } just Runs
    
    val result = urlService.createShortKey(originalUrl, Duration.ONE_DAY)
    
    assert(result == shortKey)
    verify { urlRepository.save(any()) }
    verify { urlCacheRepository.set(shortKey, originalUrl) }
  }
  
  @Test
  @DisplayName("이미 존재하는 URL에 대해 기존 Short Key를 반환한다")
  fun createShortKeyReturnsExistingShortKey() {
    val originalUrl = OriginalUrl("https://example.com")
    val existingShortKey = "existing123"
    val url = UrlFixture.createUrl(id = 1L, originalUrl = originalUrl, shortKey = existingShortKey)
    
    every { authenticationContext.getCurrentUserIdAllowNull() } returns null
    every { urlRepository.findByOriginalUrlAndCreatedBy(originalUrl, "anonymous") } returns url
    every { urlCacheRepository.set(existingShortKey, originalUrl) } just Runs
    
    val result = urlService.createShortKey(originalUrl, Duration.NONE)
    
    assert(result == existingShortKey)
    verify(exactly = 0) { urlRepository.save(any()) }
    verify { urlCacheRepository.set(existingShortKey, originalUrl) }
  }
  
  @Test
  @DisplayName("내 URL 목록을 조회할 수 있다")
  fun findMyUrls() {
    val userId = UUID.randomUUID()
    val user = UserFixture.createUser(id = userId, email = Email("test@example.com"))
    val pageable = PageRequest.of(0, 10)
    val urlDtos = listOf(
      UrlWithClickCountDto(
        id = 1L,
        shortKey = "abc123",
        originalUrl = OriginalUrl("https://example.com"),
        clickCount = 5L,
        createdAt = LocalDateTime.now(),
        createdBy = UUID.randomUUID().toString(),
        lastUsedAt = LocalDateTime.now(),
        expiresAt = LocalDateTime.now().plusDays(1)
      )
    )
    val page = PageImpl(urlDtos, pageable, 1)
    
    every { authenticationContext.getCurrentUserId() } returns userId
    every { userRepository.findById(userId) } returns Optional.of(user)
    every { urlRepository.findByCreatedByWithClickCount(userId.toString(), pageable) } returns page
    
    val result = urlService.findMyUrls(pageable)
    
    assert(result.content.size == 1)
    assert(result.content[0].shortKey == "abc123")
    verify { urlRepository.findByCreatedByWithClickCount(userId.toString(), pageable) }
  }
  
  @Test
  @DisplayName("존재하지 않는 사용자로 URL 조회 시 예외가 발생한다")
  fun findMyUrlsWithNonExistingUserThrowsException() {
    val userId = UUID.randomUUID()
    val pageable = PageRequest.of(0, 10)
    
    every { authenticationContext.getCurrentUserId() } returns userId
    every { userRepository.findById(userId) } returns Optional.empty()
    
    assertThrows<UserNotFoundException> {
      urlService.findMyUrls(pageable)
    }
  }
  
  @Test
  @DisplayName("사용하지 않은 URL을 삭제할 수 있다")
  fun deleteUnusedUrls() {
    val thresholdMonths = 6L
    val deletedCount = 10
    
    every { urlRepository.deleteByLastUsedAtBeforeAndExpiresAtIsNull(any()) } returns deletedCount
    
    val result = urlService.deleteUnusedUrls(thresholdMonths)
    
    assert(result == deletedCount)
    verify { urlRepository.deleteByLastUsedAtBeforeAndExpiresAtIsNull(any()) }
  }
  
  @Test
  @DisplayName("만료된 URL을 삭제할 수 있다")
  fun deleteExpiredUrls() {
    val deletedCount = 5
    
    every { urlRepository.deleteByExpiresAtBefore(any()) } returns deletedCount
    
    val result = urlService.deleteExpiredUrls()
    
    assert(result == deletedCount)
    verify { urlRepository.deleteByExpiresAtBefore(any()) }
  }
  
  @Test
  @DisplayName("내 URL을 삭제할 수 있다")
  fun deleteMyUrl() {
    val userId = UUID.randomUUID()
    val shortKey = "abc123"
    
    every { authenticationContext.getCurrentUserId() } returns userId
    every { urlRepository.deleteByShortKeyAndCreatedBy(shortKey, userId.toString()) } returns 1
    
    urlService.deleteMyUrl(shortKey)
    
    verify { urlRepository.deleteByShortKeyAndCreatedBy(shortKey, userId.toString()) }
    verify { eventPublisher.publishEvent(any<UrlEvent.UrlDeleted>()) }
  }
  
  @Test
  @DisplayName("존재하지 않는 URL 삭제 시 예외가 발생한다")
  fun deleteMyUrlWithNonExistingUrlThrowsException() {
    val userId = UUID.randomUUID()
    val shortKey = "nonexisting"
    
    every { authenticationContext.getCurrentUserId() } returns userId
    every { urlRepository.deleteByShortKeyAndCreatedBy(shortKey, userId.toString()) } returns 0
    
    assertThrows<ShortUrlNotFoundException> {
      urlService.deleteMyUrl(shortKey)
    }
  }
  
  @Test
  @DisplayName("캐시된 URL로 클릭할 수 있다")
  fun clickWithCachedUrl() {
    val shortKey = "abc123"
    val originalUrlString = "https://example.com"
    val urlClickDto = UrlClickDto(
      shortKey = shortKey,
      ipAddress = "127.0.0.1",
      userAgent = "Mozilla",
      referer = null
    )
    
    every { urlCacheRepository.get(shortKey) } returns originalUrlString
    
    val result = urlService.click(urlClickDto)
    
    assert(result.value == originalUrlString)
    verify { eventPublisher.publishEvent(any<UrlEvent.UrlClicked>()) }
    verify(exactly = 0) { urlRepository.findByShortKey(any()) }
  }
  
  @Test
  @DisplayName("캐시되지 않은 URL로 클릭 시 DB에서 조회한다")
  fun clickWithNonCachedUrl() {
    val shortKey = "abc123"
    val originalUrl = OriginalUrl("https://example.com")
    val url = UrlFixture.createUrl(id = 1L, originalUrl = originalUrl, shortKey = shortKey)
    val urlClickDto = UrlClickDto(
      shortKey = shortKey,
      ipAddress = "127.0.0.1",
      userAgent = "Mozilla",
      referer = null
    )
    
    every { urlCacheRepository.get(shortKey) } returns null
    every { urlRepository.findByShortKey(shortKey) } returns url
    every { urlCacheRepository.set(shortKey, originalUrl) } just Runs
    
    val result = urlService.click(urlClickDto)
    
    assert(result == originalUrl)
    verify { urlRepository.findByShortKey(shortKey) }
    verify { urlCacheRepository.set(shortKey, originalUrl) }
    verify { eventPublisher.publishEvent(any<UrlEvent.UrlClicked>()) }
  }
  
  @Test
  @DisplayName("존재하지 않는 Short Key로 클릭 시 예외가 발생한다")
  fun clickWithNonExistingShortKeyThrowsException() {
    val shortKey = "nonexisting"
    val urlClickDto = UrlClickDto(
      shortKey = shortKey,
      ipAddress = "127.0.0.1",
      userAgent = "Mozilla",
      referer = null
    )
    
    every { urlCacheRepository.get(shortKey) } returns null
    every { urlRepository.findByShortKey(shortKey) } returns null
    
    assertThrows<ShortUrlNotFoundException> {
      urlService.click(urlClickDto)
    }
  }
}

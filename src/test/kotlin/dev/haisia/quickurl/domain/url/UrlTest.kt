package dev.haisia.quickurl.domain.url

import dev.haisia.quickurl.domain.Duration
import dev.haisia.quickurl.domain.IdNotGeneratedException
import dev.haisia.quickurl.fixture.UrlFixture
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

@DisplayName("Url 테스트")
class UrlTest {
  
  @Test
  @DisplayName("Url 객체를 생성할 수 있다")
  fun createUrl() {
    val originalUrl = OriginalUrl("https://example.com")
    
    assert(Url.of(originalUrl = originalUrl).originalUrl == originalUrl)
  }
  
  @Test
  @DisplayName("만료 기간이 없는 Url을 생성하면 expiresAt이 null이다")
  fun createUrlWithNoDuration() {
    assert(UrlFixture.createUrl().expiresAt == null)
  }
  
  @Test
  @DisplayName("만료 기간이 있는 Url을 생성하면 expiresAt이 설정된다")
  fun createUrlWithDuration() {
    assert(UrlFixture.createUrl(expirationDuration = Duration.ONE_DAY).expiresAt != null)
  }
  
  @Test
  @DisplayName("생성자 ID가 없으면 anonymous, 있으면 해당 값으로 설정된다")
  fun createdByIsSetCorrectly() {
    assert(UrlFixture.createUrl(createdBy = null).createdBy == "anonymous")
    
    val createdBy = UUID.randomUUID()
    assert(UrlFixture.createUrl(createdBy = createdBy).createdBy == createdBy.toString())
  }
  
  @Test
  @DisplayName("click 메소드 호출 시 lastUsedAt이 갱신된다")
  fun clickUpdatesLastUsedAt() {
    val lastUsedAt = LocalDateTime.now().minusDays(1)
    UrlFixture.createUrl(lastUsedAt = lastUsedAt).let {
      it.click()
      assert(it.lastUsedAt != lastUsedAt)
    }
  }
  
  @Test
  @DisplayName("shortKey가 없을 때 generateShortKey를 호출하면 shortKey가 생성된다")
  fun generateShortKeyWhenNotExists() {
    val urlEncoder = mockk<UrlEncoder>().apply {
      every { encode(any()) } returns "encoded_key"
    }
    
    UrlFixture.createUrl(id = 1L).let {
      assert(it.shortKey == null)
      it.generateShortKey(urlEncoder)
      assert(it.shortKey == "encoded_key")
    }
  }
  
  @Test
  @DisplayName("shortKey가 이미 있을 때 generateShortKey를 호출해도 변경되지 않는다")
  fun generateShortKeyWhenAlreadyExists() {
    val urlEncoder = mockk<UrlEncoder>().apply {
      every { encode(any()) } returns "encoded_key"
    }
    
    UrlFixture.createUrl(id = 1L, shortKey = "alreadyShortKey").let {
      it.generateShortKey(urlEncoder)
      assert(it.shortKey == "alreadyShortKey")
    }
  }
  
  @Test
  @DisplayName("ID가 null일 때 generateShortKey 호출 시 예외가 발생한다")
  fun generateShortKeyWithNullIdThrowsException() {
    UrlFixture.createUrl(id = null).let {
      assertThrows<ShortKeyGenerationException> {
        it.generateShortKey(mockk())
      }
    }
  }
  
  @Test
  @DisplayName("ID가 존재하면 getIdOrThrow로 ID를 가져올 수 있다")
  fun getIdOrThrowReturnsIdWhenExists() {
    assert(UrlFixture.createUrl(id = 1L).getIdOrThrow() == 1L)
  }
  
  @Test
  @DisplayName("ID가 null이면 getIdOrThrow에서 예외가 발생한다")
  fun getIdOrThrowThrowsExceptionWhenIdIsNull() {
    UrlFixture.createUrl(id = null).let {
      assertThrows<IdNotGeneratedException> {
        it.getIdOrThrow()
      }
    }
  }
  
  @Test
  @DisplayName("shortKey가 존재하면 getShortKeyOrThrow로 shortKey를 가져올 수 있다")
  fun getShortKeyOrThrowReturnsShortKeyWhenExists() {
    assert(UrlFixture.createUrl(id = 1L, shortKey = "shortKey").getShortKeyOrThrow() == "shortKey")
  }
  
  @Test
  @DisplayName("shortKey가 null이면 getShortKeyOrThrow에서 예외가 발생한다")
  fun getShortKeyOrThrowThrowsExceptionWhenShortKeyIsNull() {
    UrlFixture.createUrl(id = 1L, shortKey = null).let {
      assertThrows<ShortKeyNotGeneratedException> {
        it.getShortKeyOrThrow()
      }
    }
  }
  
  @Test
  @DisplayName("shortKey가 있으면 hasShortKey는 true를 반환한다")
  fun hasShortKeyReturnsTrueWhenShortKeyExists() {
    assert(UrlFixture.createUrl(id = 1L, shortKey = "shortKey").hasShortKey())
  }
  
  @Test
  @DisplayName("shortKey가 없으면 hasShortKey는 false를 반환한다")
  fun hasShortKeyReturnsFalseWhenShortKeyIsNull() {
    assert(!UrlFixture.createUrl(id = 1L, shortKey = null).hasShortKey())
  }
  
  @Test
  @DisplayName("같은 ID를 가진 Url 객체는 동등하다")
  fun urlsWithSameIdAreEqual() {
    assert(UrlFixture.createUrl(id = 1L) == UrlFixture.createUrl(id = 1L))
  }
  
  @Test
  @DisplayName("다른 ID를 가진 Url 객체는 동등하지 않다")
  fun urlsWithDifferentIdAreNotEqual() {
    assert(UrlFixture.createUrl(id = 1L) != UrlFixture.createUrl(id = 2L))
  }
  
  @Test
  @DisplayName("ID가 null인 Url 객체는 다른 객체와 동등하지 않다")
  fun urlWithNullIdIsNotEqual() {
    assert(UrlFixture.createUrl(id = 1L) != UrlFixture.createUrl(id = null))
    
  }
}
package dev.haisia.quickurl.domain.url

import dev.haisia.quickurl.fixture.UrlFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("UrlClickLog 테스트")
class UrlClickLogTest {
  
  @Test
  @DisplayName("UrlClickLog 객체를 생성할 수 있다")
  fun createUrlClickLog() {
    val shortKey = "test"
    val ipAddress = "127.0.0.1"
    val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36"
    val referer = "https://example.com/"
    
    UrlClickLog.of(shortKey, ipAddress, userAgent, referer).let {
      assert(it.shortKey == shortKey)
      assert(it.ipAddress == ipAddress)
      assert(it.userAgent == userAgent)
      assert(it.referer == referer)
    }
  }
  
  @Suppress("SENSELESS_COMPARISON")
  @Test
  @DisplayName("UrlClickLog 생성 시 clickedAt이 자동으로 설정된다")
  fun clickedAtIsSetAutomatically() {
    assert(UrlFixture.createUrlClickLog().clickedAt != null)
  }
  
  @Test
  @DisplayName("같은 ID를 가진 UrlClickLog 객체는 동등하다")
  fun urlClickLogsWithSameIdAreEqual() {
    assert(UrlFixture.createUrlClickLog(id = 1L) == UrlFixture.createUrlClickLog(id = 1L))
  }
  
  @Test
  @DisplayName("다른 ID를 가진 UrlClickLog 객체는 동등하지 않다")
  fun urlClickLogsWithDifferentIdAreNotEqual() {
    assert(UrlFixture.createUrlClickLog(id = 1L) != UrlFixture.createUrlClickLog(id = 2L))
  }
  
  @Test
  @DisplayName("ID가 null인 UrlClickLog 객체는 다른 객체와 동등하지 않다")
  fun urlClickLogWithNullIdIsNotEqual() {
    assert(UrlFixture.createUrlClickLog(id = 1L) != UrlFixture.createUrlClickLog(id = null))
  }
}
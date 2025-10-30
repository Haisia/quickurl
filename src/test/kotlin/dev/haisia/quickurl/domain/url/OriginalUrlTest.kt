package dev.haisia.quickurl.domain.url

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("OriginalUrl 테스트")
class OriginalUrlTest {
  
  @ParameterizedTest
  @ValueSource(strings = [
    "http://example.com",
    "https://example.com",
    "http://example.com/path",
    "https://example.com/path?query=value",
    "http://subdomain.example.com",
    "https://example.com:8080/path",
    "http://example.com/path#fragment"
  ])
  @DisplayName("유효한 URL로 OriginalUrl 객체를 생성할 수 있다")
  fun createOriginalUrlWithValidUrl(url: String) {
    assert(OriginalUrl(url).value == url)
  }
  
  @ParameterizedTest
  @ValueSource(strings = [
    "",
    "   ",
    "\t\n"
  ])
  @DisplayName("빈 값이나 공백으로 OriginalUrl 생성 시 예외가 발생한다")
  fun createOriginalUrlWithBlankValueThrowsException(url: String) {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(url)
    }
  }
  
  @Test
  @DisplayName("최대 길이를 초과하는 URL로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithTooLongUrlThrowsException() {
    val tooLongUrl = "https://${"a".repeat(3000)}.com"
    
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(tooLongUrl)
    }
  }
  
  @ParameterizedTest
  @ValueSource(strings = [
    "http://example.com/<script>alert('xss')</script>",
    "javascript:alert('xss')",
    "http://example.com?onerror=alert(1)",
    "http://example.com?onload=malicious()",
    "http://example.com?onclick=hack()",
    "http://example.com?onmouseover=steal()",
    "http://example.com?query=eval(code)",
    "http://example.com?style=expression(alert(1))"
  ])
  @DisplayName("XSS 패턴이 포함된 URL로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithXssPatternThrowsException(url: String) {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(url)
    }
  }
  
  @ParameterizedTest
  @ValueSource(strings = [
    "not a url",
    "htp://wrong",
    "://missing-scheme"
  ])
  @DisplayName("잘못된 URI 형식으로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithInvalidUriFormatThrowsException(url: String) {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(url)
    }
  }
  
  @ParameterizedTest
  @ValueSource(strings = [
    "javascript:void(0)",
    "data:text/html,<script>alert(1)</script>",
    "vbscript:msgbox(1)",
    "file:///etc/passwd",
    "about:blank",
    "blob:https://example.com/uuid"
  ])
  @DisplayName("차단된 프로토콜로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithBlockedProtocolThrowsException(url: String) {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(url)
    }
  }
  
  @ParameterizedTest
  @ValueSource(strings = [
    "ftp://example.com",
    "mailto:user@example.com",
    "tel:+1234567890",
    "ssh://server.com"
  ])
  @DisplayName("허용되지 않은 프로토콜로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithNotAllowedProtocolThrowsException(url: String) {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(url)
    }
  }
  
  @Test
  @DisplayName("프로토콜이 없는 URL로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithoutProtocolThrowsException() {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl("example.com")
    }
  }
  
  @ParameterizedTest
  @ValueSource(strings = [
    "http://",
    "https://",
    "http:///path"
  ])
  @DisplayName("호스트가 없는 URL로 생성 시 예외가 발생한다")
  fun createOriginalUrlWithoutHostThrowsException(url: String) {
    assertThrows<InvalidOriginalUrlException> {
      OriginalUrl(url)
    }
  }
  
  @Test
  @DisplayName("toString은 원본 URL 값을 반환한다")
  fun toStringReturnsOriginalValue() {
    val url = "https://example.com"
    assert(OriginalUrl(url).toString() == url)
  }
  
  @Test
  @DisplayName("동일한 URL 값을 가진 OriginalUrl 객체는 동등하다")
  fun originalUrlsWithSameValueAreEqual() {
    val url = "https://example.com"
    assert(OriginalUrl(url) == OriginalUrl(url))
  }
}
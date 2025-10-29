package dev.haisia.quickurl.domain.url

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

class OriginalUrlTest {

  @Test
  @DisplayName("유효한 HTTP URL은 성공적으로 생성된다")
  fun `valid http url should be created successfully`() {
    // given
    val validUrl = "http://example.com"

    // when
    val originalUrl = OriginalUrl(validUrl)

    // then
    assertThat(originalUrl.value).isEqualTo(validUrl)
  }

  @Test
  @DisplayName("유효한 HTTPS URL은 성공적으로 생성된다")
  fun `valid https url should be created successfully`() {
    // given
    val validUrl = "https://www.google.com/search?q=kotlin"

    // when
    val originalUrl = OriginalUrl(validUrl)

    // then
    assertThat(originalUrl.value).isEqualTo(validUrl)
  }

  @Test
  @DisplayName("빈 URL은 예외를 발생시킨다")
  fun `empty url should throw exception`() {
    assertThatThrownBy { OriginalUrl("") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("cannot be empty")
  }

  @Test
  @DisplayName("공백만 있는 URL은 예외를 발생시킨다")
  fun `blank url should throw exception`() {
    assertThatThrownBy { OriginalUrl("   ") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("cannot be empty")
  }

  @Test
  @DisplayName("프로토콜이 없는 URL은 예외를 발생시킨다")
  fun `url without protocol should throw exception`() {
    assertThatThrownBy { OriginalUrl("example.com") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("must have a protocol")
  }

  @Test
  @DisplayName("잘못된 URL 형식은 예외를 발생시킨다")
  fun `invalid url format should throw exception`() {
    assertThatThrownBy { OriginalUrl("aaaa") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
  }

  @Test
  @DisplayName("javascript 프로토콜은 차단된다")
  fun `javascript protocol should be blocked`() {
    assertThatThrownBy { OriginalUrl("javascript:alert('xss')") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("not allowed for security reasons")
  }

  @Test
  @DisplayName("data 프로토콜은 차단된다")
  fun `data protocol should be blocked`() {
    assertThatThrownBy { OriginalUrl("data:text/html,<script>alert('xss')</script>") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("not allowed for security reasons")
  }

  @Test
  @DisplayName("XSS 패턴이 포함된 URL은 차단된다")
  fun `url with xss pattern should be blocked`() {
    assertThatThrownBy { OriginalUrl("http://example.com?param=<script>alert('xss')</script>") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("potentially malicious content")
  }

  @Test
  @DisplayName("onerror 이벤트가 포함된 URL은 차단된다")
  fun `url with onerror event should be blocked`() {
    assertThatThrownBy { OriginalUrl("http://example.com?img=x onerror=alert('xss')") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("potentially malicious content")
  }

  @Test
  @DisplayName("호스트가 없는 URL은 예외를 발생시킨다")
  fun `url without host should throw exception`() {
    assertThatThrownBy { OriginalUrl("http://") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("must have a valid host")
  }

  @Test
  @DisplayName("FTP 프로토콜은 허용되지 않는다")
  fun `ftp protocol should not be allowed`() {
    assertThatThrownBy { OriginalUrl("ftp://ftp.example.com/file.txt") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("Only http and https protocols are allowed")
  }

  @Test
  @DisplayName("매우 긴 URL은 차단된다")
  fun `very long url should be blocked`() {
    val longUrl = "https://example.com/" + "a".repeat(3000)

    assertThatThrownBy { OriginalUrl(longUrl) }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("too long")
  }

  @Test
  @DisplayName("쿼리 파라미터가 있는 유효한 URL은 성공적으로 생성된다")
  fun `valid url with query parameters should be created successfully`() {
    // given
    val validUrl = "https://example.com/path?param1=value1&param2=value2"

    // when
    val originalUrl = OriginalUrl(validUrl)

    // then
    assertThat(originalUrl.value).isEqualTo(validUrl)
  }

  @Test
  @DisplayName("프래그먼트가 있는 유효한 URL은 성공적으로 생성된다")
  fun `valid url with fragment should be created successfully`() {
    // given
    val validUrl = "https://example.com/path#section"

    // when
    val originalUrl = OriginalUrl(validUrl)

    // then
    assertThat(originalUrl.value).isEqualTo(validUrl)
  }

  @Test
  @DisplayName("포트 번호가 있는 유효한 URL은 성공적으로 생성된다")
  fun `valid url with port should be created successfully`() {
    // given
    val validUrl = "https://example.com:8080/path"

    // when
    val originalUrl = OriginalUrl(validUrl)

    // then
    assertThat(originalUrl.value).isEqualTo(validUrl)
  }

  @Test
  @DisplayName("toString은 원본 URL 값을 반환한다")
  fun `toString should return original url value`() {
    // given
    val validUrl = "https://example.com"
    val originalUrl = OriginalUrl(validUrl)

    // when
    val result = originalUrl.toString()

    // then
    assertThat(result).isEqualTo(validUrl)
  }

  @Test
  @DisplayName("잘못된 프로토콜 이름은 차단된다 - http로 시작하지만 다른 문자 포함")
  fun `invalid protocol starting with http should be blocked`() {
    assertThatThrownBy { OriginalUrl("httpabc://example.com") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("Only http and https protocols are allowed")
  }

  @Test
  @DisplayName("잘못된 프로토콜 이름은 차단된다 - http를 포함하지만 앞에 문자 추가")
  fun `invalid protocol containing http should be blocked`() {
    assertThatThrownBy { OriginalUrl("abchttp://example.com") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("Only http and https protocols are allowed")
  }

  @Test
  @DisplayName("잘못된 프로토콜 이름은 차단된다 - https로 시작하지만 다른 문자 포함")
  fun `invalid protocol starting with https should be blocked`() {
    assertThatThrownBy { OriginalUrl("httpsxyz://example.com") }
      .isInstanceOf(InvalidOriginalUrlException::class.java)
      .hasMessageContaining("Only http and https protocols are allowed")
  }
}

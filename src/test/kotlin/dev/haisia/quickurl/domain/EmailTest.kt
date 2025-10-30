package dev.haisia.quickurl.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Email 테스트")
class EmailTest {

  @ParameterizedTest
  @ValueSource(
    strings = [
      "test@example.com",
      "user.name@example.com",
      "user+tag@example.co.kr",
      "user_123@test-domain.com",
      "a@b.co"
    ]
  )
  @DisplayName("유효한 이메일 형식으로 Email 객체를 생성할 수 있다")
  fun createEmailWithValidFormat(email: String) {
    assert(Email(email).value == email)
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "invalid.email",
      "@example.com",
      "user@",
      "user@domain",
      "user name@example.com",
      "user@domain..com",
      ""
    ]
  )
  @DisplayName("유효하지 않은 이메일 형식으로 Email 객체 생성 시 예외가 발생한다")
  fun createEmailWithInvalidFormatThrowsException(email: String) {
    assertThrows<IllegalArgumentException> {
      Email(email)
    }
  }
}
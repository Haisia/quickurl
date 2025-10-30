package dev.haisia.quickurl.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Password 테스트")
class PasswordTest {

  @ParameterizedTest
  @ValueSource(
    strings = [
      "123456",
      "password",
      "abcdef",
      "!@#$%^&*()",
      "VeryLongPasswordWithManyCharacters123456"
    ]
  )
  @DisplayName("6자 이상의 유효한 비밀번호로 Password 객체를 생성할 수 있다")
  fun createPasswordWithValidFormat(password: String) {
    assert(Password(password).value == password)
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "12345",
      "abc",
      "a",
      "",
      "pass word",
      "   ",
      "pass word123"
    ]
  )
  @DisplayName("6자 미만이거나 공백이 포함된 비밀번호로 Password 객체 생성 시 예외가 발생한다")
  fun createPasswordWithInvalidFormatThrowsException(password: String) {
    assertThrows<IllegalArgumentException> {
      Password(password)
    }
  }

  @Test
  @DisplayName("Password 객체는 동일한 값일 때 동등하다")
  fun passwordsWithSameValueAreEqual() {
    val password = "mypassword"
    assert(Password(password) == Password(password))
  }

  @Test
  @DisplayName("Password 객체는 다른 값일 때 동등하지 않다")
  fun passwordsWithDifferentValueAreNotEqual() {
    val password = "mypassword"
    val anotherPassword = "yourpassword"
    assert(Password(password) != Password(anotherPassword))
  }

  @Test
  @DisplayName("Password 객체의 hashCode는 동일한 값일 때 같다")
  fun passwordsWithSameValueHaveSameHashCode() {
    val password = "mypassword"
    assert(Password(password).hashCode() == Password(password).hashCode())
  }

}

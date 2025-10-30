package dev.haisia.quickurl.domain.user

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("UserDomainExceptionTest 테스트")
class UserDomainExceptionTest {
  @Test
  @DisplayName("UserIdMissingException은 기본 메시지로 생성할 수 있다")
  fun createUserIdMissingExceptionWithDefaultMessage() {
    assert(
      UserIdMissingException().message
          == "User ID is required but was found to be null. Check the entity persistence status."
    )
  }

  @Test
  @DisplayName("UserIdMissingException은 커스텀 메시지로 생성할 수 있다")
  fun createUserIdMissingExceptionWithCustomMessage() {
    assert(UserIdMissingException("Custom message").message == "Custom message")
  }
}
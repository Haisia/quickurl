package dev.haisia.quickurl.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("DomainException 테스트")
class DomainExceptionTest {

  @Test
  @DisplayName("IdNotGeneratedException은 기본 메시지로 생성할 수 있다")
  fun createIdNotGeneratedExceptionWithDefaultMessage() {
    assert(IdNotGeneratedException().message == "Id not generated.")
  }

  @Test
  @DisplayName("IdNotGeneratedException은 커스텀 메시지로 생성할 수 있다")
  fun createIdNotGeneratedExceptionWithCustomMessage() {
    assert(IdNotGeneratedException("Custom message").message == "Custom message")
  }
}

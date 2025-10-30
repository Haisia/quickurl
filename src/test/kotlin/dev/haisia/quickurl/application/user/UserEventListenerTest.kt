package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.application.shared.out.EmailSender
import dev.haisia.quickurl.domain.Email
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

@DisplayName("UserEventListener 테스트")
class UserEventListenerTest {
  
  private val emailSender: EmailSender = mockk()
  private val userEventListener = UserEventListener(emailSender)
  
  @Test
  @DisplayName("UserCreated 이벤트 발생 시 환영 이메일을 전송한다")
  fun handleUserCreatedSendsWelcomeEmail() {
    val email = Email("test@example.com")
    val event = UserEvent.UserCreated(email)
    
    every { emailSender.sendWelcome(any(), any()) } returns Mono.empty()
    
    userEventListener.handleUserCreated(event)
    
    verify(exactly = 1) { emailSender.sendWelcome(email.value, email.value) }
  }
  
  @Test
  @DisplayName("환영 이메일 전송 시 올바른 이메일 주소를 사용한다")
  fun handleUserCreatedUsesCorrectEmailAddress() {
    val email = Email("user@test.com")
    val event = UserEvent.UserCreated(email)
    
    every { emailSender.sendWelcome(any(), any()) } returns Mono.empty()
    
    userEventListener.handleUserCreated(event)
    
    verify { emailSender.sendWelcome(email.value, email.value) }
  }
  
  @Test
  @DisplayName("이메일 전송이 실패해도 예외가 전파되지 않는다")
  fun handleUserCreatedDoesNotPropagateEmailSendingException() {
    val email = Email("fail@example.com")
    val event = UserEvent.UserCreated(email)
    
    every { emailSender.sendWelcome(any(), any()) } returns Mono.error(RuntimeException("Email send failed"))
    
    userEventListener.handleUserCreated(event)
    
    verify { emailSender.sendWelcome(email.value, email.value) }
  }
}
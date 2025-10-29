package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.application.shared.out.EmailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserEventListener(
  private val emailSender: EmailSender
) {

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleUserCreated(event: UserEvent.UserCreated) {
    emailSender.sendWelcome(event.userEmail.value, event.userEmail.value)
      .subscribe()
  }

}
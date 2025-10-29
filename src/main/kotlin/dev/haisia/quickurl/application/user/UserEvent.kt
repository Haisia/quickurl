package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.domain.Email

sealed class UserEvent {
  data class UserCreated(val userEmail: Email) : UserEvent()
}
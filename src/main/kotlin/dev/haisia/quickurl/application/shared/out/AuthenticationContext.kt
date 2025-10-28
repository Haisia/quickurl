package dev.haisia.quickurl.application.shared.out

import dev.haisia.quickurl.domain.Email
import java.util.UUID

interface AuthenticationContext {
  fun getCurrentUserId(): UUID
  fun getCurrentEmail(): Email
  fun isAuthenticated(): Boolean
  fun getCurrentUserIdAllowNull(): UUID?
}
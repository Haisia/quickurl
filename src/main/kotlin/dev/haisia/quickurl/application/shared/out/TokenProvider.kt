package dev.haisia.quickurl.application.shared.out

import dev.haisia.quickurl.domain.Email
import java.util.*

interface TokenProvider {
  fun generateAccessToken(userId: UUID, email: Email): String
  fun generateRefreshToken(userId: UUID): String
}
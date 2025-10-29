package dev.haisia.quickurl.application.shared.out

import dev.haisia.quickurl.domain.Email
import java.time.LocalDateTime
import java.util.*

interface TokenResolver {
  fun getUserIdFromToken(token: String): UUID

  fun getEmailFromToken(token: String): Email

  fun validateToken(token: String): Boolean

  fun validateAsAccessToken(token: String): Boolean

  fun validateAsRefreshToken(token: String): Boolean

  fun getTokenType(token: String): String

  fun getExpirationDate(token: String): Date

  fun getExpirationDateTime(token: String): LocalDateTime

  fun isTokenExpired(token: String): Boolean
}
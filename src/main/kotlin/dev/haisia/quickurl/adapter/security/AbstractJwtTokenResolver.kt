package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.application.shared.out.TokenResolver
import dev.haisia.quickurl.domain.Email
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

abstract class AbstractJwtTokenResolver : TokenResolver {

  protected abstract fun getSecretKey(): String

  private val key: SecretKey by lazy {
    Keys.hmacShaKeyFor(getSecretKey().toByteArray())
  }

  companion object {
    const val TOKEN_TYPE_CLAIM = "type"
    const val EMAIL_CLAIM = "email"
  }

  override fun getUserIdFromToken(token: String): UUID {
    return UUID.fromString(getClaims(token).subject)
  }

  override fun getEmailFromToken(token: String): Email {
    return Email(getClaims(token).get(EMAIL_CLAIM, String::class.java))
  }

  override fun validateToken(token: String): Boolean {
    return try {
      getClaims(token)
      true
    } catch (_: ExpiredJwtException) {
      false
    } catch (_: UnsupportedJwtException) {
      false
    } catch (_: MalformedJwtException) {
      false
    } catch (_: SignatureException) {
      false
    } catch (_: IllegalArgumentException) {
      false
    }
  }

  override fun getTokenType(token: String): String {
    return getClaims(token).get(TOKEN_TYPE_CLAIM, String::class.java)
  }

  override fun getExpirationDate(token: String): Date {
    return getClaims(token).expiration
  }

  override fun getExpirationDateTime(token: String): LocalDateTime {
    return getExpirationDate(token)
      .toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime()
  }

  override fun isTokenExpired(token: String): Boolean {
    return try {
      getExpirationDateTime(token).isBefore(LocalDateTime.now())
    } catch (_: ExpiredJwtException) {
      true
    }
  }

  protected fun getClaims(token: String): Claims {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .payload
  }
}
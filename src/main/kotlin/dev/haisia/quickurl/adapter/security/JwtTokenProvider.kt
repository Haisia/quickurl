package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.application.shared.out.TokenProvider
import dev.haisia.quickurl.domain.Email
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.util.*
import javax.crypto.SecretKey

@Configuration
class JwtTokenProvider(
  @Value("\${jwt.secret}") private val secretKey: String,

  @Value("\${jwt.access-token.validity-second}") private val accessTokenValiditySecond: Long,

  @Value("\${jwt.refresh-token.validity-second}") private val refreshTokenValiditySecond: Long,

  ) : TokenProvider {

  private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

  companion object {
    const val ACCESS_TYPE = "access"
    const val REFRESH_TYPE = "refresh"
    const val TOKEN_TYPE_CLAIM = "type"
    const val EMAIL_CLAIM = "email"
  }

  override fun generateAccessToken(userId: UUID, email: Email): String {
    val now = Date()
    val expiryDate = Date(now.time + accessTokenValiditySecond * 1000)

    return Jwts.builder()
      .subject(userId.toString())
      .claim(EMAIL_CLAIM, email.value)
      .claim(TOKEN_TYPE_CLAIM, ACCESS_TYPE)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(key)
      .compact()
  }

  override fun generateRefreshToken(userId: UUID): String {
    val now = Date()
    val expiryDate = Date(now.time + refreshTokenValiditySecond * 1000)

    return Jwts.builder()
      .subject(userId.toString())
      .claim(TOKEN_TYPE_CLAIM, REFRESH_TYPE)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(key)
      .compact()
  }
}
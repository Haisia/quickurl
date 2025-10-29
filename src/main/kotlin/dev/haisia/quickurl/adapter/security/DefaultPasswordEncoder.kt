package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.domain.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class DefaultPasswordEncoder(
  val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
): PasswordEncoder {

  override fun encode(password: String): String {
    return bCryptPasswordEncoder.encode(password)
  }

  override fun matches(password: String, hashedPassword: String): Boolean {
    return bCryptPasswordEncoder.matches(password, hashedPassword)
  }

}
package dev.haisia.quickurl.fixture

import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.PasswordEncoder
import dev.haisia.quickurl.domain.url.Url
import dev.haisia.quickurl.domain.user.User
import dev.haisia.quickurl.fixture.TestReflectionUtils.setFieldValue
import java.time.LocalDateTime
import java.util.UUID

class UserFixture {
  
  companion object {
    fun createUser(
      id: UUID? = null,
      email: Email = Email("test@test.com"),
      password: Password = Password("<PASSWORD>"),
      passwordEncoder: PasswordEncoder = this.passwordEncoder,
      hashedPassword: String? = null,
    ): User {
      
      val user = User.of(
        email = email,
        password = password,
        passwordEncoder = passwordEncoder
      )

      id?.let { user.setFieldValue("id", it) }
      hashedPassword?.let { user.setFieldValue("_hashedPassword", it) }

      return user
    }
    
    private val passwordEncoder = object : PasswordEncoder {
      override fun encode(password: String) = "encoded_$password"
      override fun matches(password: String, hashedPassword: String) = encode(password) == hashedPassword
    }
  }
}

package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.application.shared.out.AuthenticationContext
import dev.haisia.quickurl.application.user.UnauthorizedException
import dev.haisia.quickurl.domain.Email
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class SecurityAuthenticationContext : AuthenticationContext {

  override fun getCurrentUserId(): UUID {
    return getUserDetails().userId
  }

  override fun getCurrentEmail(): Email {
    return getUserDetails().email
  }

  override fun isAuthenticated(): Boolean {
    val auth = SecurityContextHolder.getContext().authentication
    return auth != null && auth.isAuthenticated
  }

  override fun getCurrentUserIdAllowNull(): UUID? {
    return try {
      getUserDetails().userId
    } catch (_: UnauthorizedException) {
      null
    }
  }

  private fun getUserDetails(): CustomUserDetails {
    val authentication = SecurityContextHolder.getContext().authentication
      ?: throw UnauthorizedException("인증되지 않은 사용자입니다")

    return authentication.principal as? CustomUserDetails
      ?: throw UnauthorizedException("잘못된 인증 정보입니다")
  }
}
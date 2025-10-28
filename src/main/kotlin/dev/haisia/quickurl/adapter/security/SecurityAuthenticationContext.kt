package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.application.shared.out.AuthenticationContext
import dev.haisia.quickurl.application.user.UnauthorizedAdapterException
import dev.haisia.quickurl.domain.Email
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID

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

  private fun getUserDetails(): CustomUserDetails {
    val authentication = SecurityContextHolder.getContext().authentication
      ?: throw UnauthorizedAdapterException("인증되지 않은 사용자입니다")

    return authentication.principal as? CustomUserDetails
      ?: throw UnauthorizedAdapterException("잘못된 인증 정보입니다")
  }
}
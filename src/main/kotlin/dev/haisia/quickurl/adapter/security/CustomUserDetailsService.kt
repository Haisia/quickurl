package dev.haisia.quickurl.adapter.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService() : UserDetailsService {

  override fun loadUserByUsername(email: String): UserDetails {
    return User.builder()
      .username(email)
      .authorities(emptyList())
      .build()
  }
}
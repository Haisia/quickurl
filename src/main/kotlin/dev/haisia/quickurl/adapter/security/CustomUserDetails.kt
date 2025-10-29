package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.domain.Email
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class CustomUserDetails(
  val userId: UUID,
  val email: Email
) : UserDetails {

  override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()

  override fun getPassword(): String = ""

  override fun getUsername(): String = email.value

  fun getUserId() = userId.toString()

  override fun isAccountNonExpired(): Boolean = true

  override fun isAccountNonLocked(): Boolean = true

  override fun isCredentialsNonExpired(): Boolean = true

  override fun isEnabled(): Boolean = true
}
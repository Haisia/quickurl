package dev.haisia.quickurl.application.user.out

import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository: JpaRepository<User, UUID> {
  fun existsByEmail(email: Email): Boolean
  fun findByEmail(email: Email): User?
}
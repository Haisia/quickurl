package dev.haisia.quickurl.domain.user

import dev.haisia.quickurl.adapter.persistence.converter.EmailConverter
import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.PasswordEncoder
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Table(name = "users")
@Entity
class User private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  val id: UUID? = null,

  @Column(name = "email", nullable = false, unique = true)
  @Convert(converter = EmailConverter::class)
  val email: Email,

  @Column(name = "hashed_password", nullable = false)
  private var _hashedPassword: String,

) {

  /* 외부에서 읽기 전용으로 사용하기 위함 */
  val hashedPassword: String
    get() = _hashedPassword

  companion object {
    fun of(
      email: Email,
      password: Password,
      passwordEncoder: PasswordEncoder,
    ): User {
      val hashedPassword = passwordEncoder.encode(password.value)

      return User(
        email = email,
        _hashedPassword = hashedPassword
      )
    }
  }

  fun verifyPassword(password: Password, passwordEncoder: PasswordEncoder): Boolean {
    return passwordEncoder.matches(password.value, _hashedPassword)
  }

  fun changePassword(newPassword:Password, passwordEncoder: PasswordEncoder) {
    _hashedPassword = passwordEncoder.encode(newPassword.value)
  }

  fun getIdOrThrow(): UUID {
    return id ?: throw UserIdMissingException("User ID cannot be null for this operation.")
  }

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    val oEffectiveClass =
      if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
    val thisEffectiveClass =
      if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
    if (thisEffectiveClass != oEffectiveClass) return false
    other as User

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

}
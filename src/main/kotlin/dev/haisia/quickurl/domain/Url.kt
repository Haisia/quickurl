package dev.haisia.quickurl.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

@Table(name = "urls")
@Entity
class Url private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "short_key", nullable = false, unique = true)
  val shortKey: String,

  @Column(name = "original_url", nullable = false, unique = true)
  val originalUrl: String,

  @Column(name = "last_used_at", nullable = false)
  var lastUsedAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "is_deleted", nullable = false)
  val isDeleted: Boolean = false,
  ) {
  companion object {
    fun of(
      shortKey: String,
      originalUrl: String
    ): Url =
      Url(
        shortKey = shortKey,
        originalUrl = originalUrl
      )
  }

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    val oEffectiveClass =
      if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
    val thisEffectiveClass =
      if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
    if (thisEffectiveClass != oEffectiveClass) return false
    other as Url

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

}
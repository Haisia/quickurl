package dev.haisia.quickurl.domain.url

import dev.haisia.quickurl.domain.ShortKeyGenerationException
import dev.haisia.quickurl.domain.ShortKeyNotGeneratedException
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

@Table(name = "urls")
@Entity
class Url private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "short_key", unique = true)
  var shortKey: String? = null,

  @Column(name = "original_url", nullable = false, unique = true)
  val originalUrl: String,

  @Column(name = "last_used_at", nullable = false)
  var lastUsedAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),
) {
  companion object {
    fun of(originalUrl: String): Url = Url(originalUrl = originalUrl)
  }

  fun click() {
    this.lastUsedAt = LocalDateTime.now()
  }

  fun generateShortKey(urlEncoder: UrlEncoder) {
    if (this.shortKey != null) return

    val id = this.id ?: throw ShortKeyGenerationException()

    this.shortKey = urlEncoder.encode(id)
  }

  fun requireShortKey(): String = this.shortKey ?: throw ShortKeyNotGeneratedException()

  fun hasShortKey(): Boolean = shortKey != null

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
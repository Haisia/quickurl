package dev.haisia.quickurl.domain.url

import dev.haisia.quickurl.domain.IdNotGeneratedException
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime
import java.util.*

@Table(
  name = "urls",
  uniqueConstraints = [
    UniqueConstraint(name = "uk_ourl_user",columnNames = ["original_url","created_by"]),
  ]
)
@Entity
class Url private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "short_key", unique = true)
  var shortKey: String? = null,

  @Column(name = "original_url", nullable = false)
  val originalUrl: String,

  /* userId: UUID
  * 익명사용자를 고려하여 UUID 가 아닌 String 타입으로 정의 함
  * 추후 creatorId 라는 별도의 vo 로의 관리를 고려 해 보자.
  * */
  @Column(name = "created_by", nullable = false)
  val createdBy: String = "anonymous",

  @Column(name = "last_used_at", nullable = false)
  var lastUsedAt: LocalDateTime = LocalDateTime.now(),

  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),
) {
  companion object {
    fun of(
      originalUrl: String,
      createdBy: UUID? = null
    ): Url {
      val strCreatedBy = createdBy?.toString() ?: "anonymous"

      return Url(
        originalUrl = originalUrl,
        createdBy = strCreatedBy
      )
    }
  }

  fun click() {
    this.lastUsedAt = LocalDateTime.now()
  }

  fun generateShortKey(urlEncoder: UrlEncoder) {
    if (this.shortKey != null) return

    val id = this.id ?: throw ShortKeyGenerationException()

    this.shortKey = urlEncoder.encode(id)
  }

  fun getIdOrThrow(): Long = this.id ?: throw IdNotGeneratedException()

  fun getShortKeyOrThrow(): String = this.shortKey ?: throw ShortKeyNotGeneratedException()

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
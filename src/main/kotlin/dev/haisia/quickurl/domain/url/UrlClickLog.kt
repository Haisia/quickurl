package dev.haisia.quickurl.domain.url

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

@Table(
  name = "url_click_logs",
  indexes = [
    Index(name = "idx_short_key", columnList = "short_key"),
    Index(name = "idx_clicked_at", columnList = "clicked_at")
  ]
)
@Entity
class UrlClickLog private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "short_key", nullable = false, length = 20)
  val shortKey: String,

  @Column(name = "ip_address", length = 45, nullable = true)
  val ipAddress: String?,

  @Column(name = "user_agent", length = 500, nullable = true)
  val userAgent: String?,

  @Column(name = "referer", length = 500, nullable = true)
  val referer: String?,

  @Column(name = "clicked_at", nullable = false)
  val clickedAt: LocalDateTime = LocalDateTime.now(),
) {
  companion object {
    fun of(
      shortKey: String,
      ipAddress: String?,
      userAgent: String?,
      referer: String?
    ): UrlClickLog {
      return UrlClickLog(
        shortKey = shortKey,
        ipAddress = ipAddress,
        userAgent = userAgent,
        referer = referer
      )
    }
  }

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    val oEffectiveClass =
      if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
    val thisEffectiveClass =
      if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
    if (thisEffectiveClass != oEffectiveClass) return false
    other as UrlClickLog

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

}

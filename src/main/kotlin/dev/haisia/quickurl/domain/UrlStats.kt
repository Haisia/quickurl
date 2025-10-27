package dev.haisia.quickurl.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDateTime

@Table(name = "url_stats")
@Entity
class UrlStats private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "ip_address", nullable = false)
  val ipAddress: String,

  @Column(name = "country", nullable = false)
  val country: String,

  @Column(name = "device", nullable = false)
  val device: String,

  @Column(name = "browser", nullable = false)
  val browser: String,

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "url_id", nullable = false)
  val url: Url,

  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime = LocalDateTime.now()
) {
  companion object {
    fun of(
      ipAddress: String,
      country: String,
      device: String,
      browser: String,
      url: Url
    ): UrlStats =
      UrlStats(
        ipAddress = ipAddress,
        country = country,
        device = device,
        browser = browser,
        url = url
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
    other as UrlStats

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

}
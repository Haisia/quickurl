package dev.haisia.quickurl.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Table(
  name = "click_logs",
  indexes = [
    Index(name = "idx_short_key", columnList = "short_key"),
    Index(name = "idx_clicked_at", columnList = "clicked_at")
  ]
)
@Entity
class ClickLog private constructor(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "short_key", nullable = false, length = 20)
  val shortKey: String,

  @Column(name = "ip_address", length = 45)
  val ipAddress: String?,

  @Column(name = "user_agent", length = 500)
  val userAgent: String?,

  @Column(name = "referer", length = 500)
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
    ): ClickLog {
      return ClickLog(
        shortKey = shortKey,
        ipAddress = ipAddress,
        userAgent = userAgent,
        referer = referer
      )
    }
  }
}

package dev.haisia.quickurl.adapter.persistence.clickstats

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy

@Table(name = "click_total_stats")
@Entity
class ClickTotalStats(
  /* 고정 pk를 활용 해 row 가 1개로 유지됨을 보장 */
  @Id
  @Column(name = "id")
  val id: Long = 1L,

  @Column(name = "total_clicks", nullable = false)
  var totalClicks: Long = 0L,
) {
  fun incrementTotalClicks(amount: Long = 1L) {
    this.totalClicks += amount
  }

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    val oEffectiveClass =
      if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
    val thisEffectiveClass =
      if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
    if (thisEffectiveClass != oEffectiveClass) return false
    other as ClickTotalStats

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

}
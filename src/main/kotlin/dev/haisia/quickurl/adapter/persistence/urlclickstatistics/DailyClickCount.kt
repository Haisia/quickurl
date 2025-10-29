package dev.haisia.quickurl.adapter.persistence.urlclickstatistics

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import java.time.LocalDate

@Table(name = "daily_click_count")
@Entity
class DailyClickCount(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "daily_clicks", nullable = false)
  var dailyClicks: Long = 0L,

  @Column(name = "date", nullable = false, unique = true)
  val date: LocalDate = LocalDate.now()
) {

  fun incrementClicks(amount: Long = 1L) {
    this.dailyClicks += amount
  }

  final override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    val oEffectiveClass =
      if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
    val thisEffectiveClass =
      if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
    if (thisEffectiveClass != oEffectiveClass) return false
    other as DailyClickCount

    return id != null && id == other.id
  }

  final override fun hashCode(): Int =
    if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}

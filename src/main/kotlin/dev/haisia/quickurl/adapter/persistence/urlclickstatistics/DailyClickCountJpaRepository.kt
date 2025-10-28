package dev.haisia.quickurl.adapter.persistence.urlclickstatistics

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DailyClickCountJpaRepository: JpaRepository<DailyClickCount, Long> {
  fun findByDate(date: LocalDate): DailyClickCount?
}

package dev.haisia.quickurl.adapter.persistence.clickstats

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ClickTodayStatsJpaRepository: JpaRepository<ClickTodayStats, Long> {
  fun findByTodayDate(todayDate: LocalDate): ClickTodayStats?
}
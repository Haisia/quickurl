package dev.haisia.quickurl.adapter.persistence.clickstats

import org.springframework.data.jpa.repository.JpaRepository

interface ClickTotalStatsJpaRepository: JpaRepository<ClickTotalStats, Long> {
}
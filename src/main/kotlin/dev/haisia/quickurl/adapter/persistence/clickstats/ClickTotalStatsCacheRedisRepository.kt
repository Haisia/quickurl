package dev.haisia.quickurl.adapter.persistence.clickstats

import org.springframework.data.repository.CrudRepository

interface ClickTotalStatsCacheRedisRepository: CrudRepository<ClickTotalStatsCache, Long> {
}
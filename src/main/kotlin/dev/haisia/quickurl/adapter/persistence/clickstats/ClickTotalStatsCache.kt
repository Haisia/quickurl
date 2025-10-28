package dev.haisia.quickurl.adapter.persistence.clickstats

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("click_total_stats")
class ClickTotalStatsCache(
  @Id
  val id: Long = 1L,
  val totalClicks: Long = 0L
)
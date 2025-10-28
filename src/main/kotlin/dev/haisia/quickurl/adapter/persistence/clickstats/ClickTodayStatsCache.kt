package dev.haisia.quickurl.adapter.persistence.clickstats

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.util.concurrent.TimeUnit

@RedisHash("click_today_stats")
class ClickTodayStatsCache(
  @Id
  val todayDate: String = "",
  val todayClicks: Long = 0L,
  @TimeToLive(unit = TimeUnit.HOURS)
  val ttl: Long = 24L,
)
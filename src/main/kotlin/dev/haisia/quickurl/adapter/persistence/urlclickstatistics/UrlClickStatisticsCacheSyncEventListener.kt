package dev.haisia.quickurl.adapter.persistence.urlclickstatistics

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.Duration
import java.time.LocalDateTime

@Component
class UrlClickStatisticsCacheSyncEventListener(
  private val stringRedisTemplate: StringRedisTemplate
) {

  companion object {
    private const val CUMULATIVE_CLICKS_KEY = "url_statistics:cumulative_clicks"
    private const val DAILY_CLICKS_KEY_PREFIX = "url_statistics:daily_clicks:"
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleCumulativeClicksUpdated(event: UrlClickStatisticsCacheSyncEvent.CumulativeClicksUpdated) {
    stringRedisTemplate.opsForValue().set(CUMULATIVE_CLICKS_KEY, event.cumulativeClicks.toString())
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleDailyClicksUpdated(event: UrlClickStatisticsCacheSyncEvent.DailyClicksUpdated) {
    val key = "$DAILY_CLICKS_KEY_PREFIX${event.date}"
    stringRedisTemplate.opsForValue().set(key, event.dailyClicks.toString())
    
    // 자정까지 남은 시간을 TTL로 설정
    val now = LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    val ttl = Duration.between(now, midnight)
    stringRedisTemplate.expire(key, ttl)
  }
}

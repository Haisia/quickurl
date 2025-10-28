package dev.haisia.quickurl.adapter.persistence.urlclickstatistics

import dev.haisia.quickurl.application.url.out.UrlClickStatisticsRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Transactional
@Repository
class UrlClickStatisticsRepositoryImpl(
  private val cumulativeRepository: CumulativeClickCountJpaRepository,
  private val dailyRepository: DailyClickCountJpaRepository,
  private val stringRedisTemplate: StringRedisTemplate,
  private val eventPublisher: ApplicationEventPublisher
): UrlClickStatisticsRepository {

  companion object {
    private const val CUMULATIVE_CLICKS_KEY = "url_statistics:cumulative_clicks"
    private const val DAILY_CLICKS_KEY_PREFIX = "url_statistics:daily_clicks:"
  }

  override fun click(amount: Long) {
    val cumulativeCount = incrementCumulativeClicks(amount)
    val dailyCount = incrementDailyClicks(amount)

    // 트랜잭션 커밋 후 비동기로 캐시 동기화
    eventPublisher.publishEvent(
      UrlClickStatisticsCacheSyncEvent.CumulativeClicksUpdated(cumulativeCount.cumulativeClicks)
    )
    eventPublisher.publishEvent(
      UrlClickStatisticsCacheSyncEvent.DailyClicksUpdated(dailyCount.date, dailyCount.dailyClicks)
    )
  }

  override fun getDailyClickCount(): Long {
    val cached = stringRedisTemplate.opsForValue().get(getDailyClicksKey())?.toLongOrNull()
    if (cached != null) return cached
    
    // 캐시 미스 시 DB에서 조회 후 캐시 갱신
    val dbValue = dailyRepository.findByDate(LocalDate.now())?.dailyClicks ?: 0L
    if (dbValue > 0) {
      val key = getDailyClicksKey()
      stringRedisTemplate.opsForValue().set(key, dbValue.toString())
      
      // 자정까지 남은 시간을 TTL로 설정
      val now = LocalDateTime.now()
      val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
      val ttl = Duration.between(now, midnight)
      stringRedisTemplate.expire(key, ttl)
    }
    return dbValue
  }

  override fun getCumulativeClickCount(): Long {
    val cached = stringRedisTemplate.opsForValue().get(CUMULATIVE_CLICKS_KEY)?.toLongOrNull()
    if (cached != null) return cached
    
    // 캐시 미스 시 DB에서 조회 후 캐시 갱신
    val dbValue = cumulativeRepository.findById(1L).orElse(null)?.cumulativeClicks ?: 0L
    if (dbValue > 0) {
      stringRedisTemplate.opsForValue().set(CUMULATIVE_CLICKS_KEY, dbValue.toString())
    }
    return dbValue
  }

  private fun incrementCumulativeClicks(amount: Long): CumulativeClickCount {
    val cumulativeCount = cumulativeRepository.findById(1L).orElse(null) 
      ?: cumulativeRepository.save(CumulativeClickCount())
    
    cumulativeCount.incrementClicks(amount)
    
    return cumulativeRepository.save(cumulativeCount)
  }

  private fun incrementDailyClicks(amount: Long): DailyClickCount {
    val dailyCount = dailyRepository.findByDate(LocalDate.now()) ?: dailyRepository.save(DailyClickCount())
    dailyCount.incrementClicks(amount)

    return dailyRepository.save(dailyCount)
  }

  private fun getDailyClicksKey(date: LocalDate = LocalDate.now()): String {
    return "$DAILY_CLICKS_KEY_PREFIX$date"
  }
}

package dev.haisia.quickurl.adapter.persistence.clickstats

import dev.haisia.quickurl.application.url.out.ClickStatsRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.jvm.optionals.getOrNull

@Transactional
@Repository
class ClickStatsRepositoryImpl(
  private val totalRepository: ClickTotalStatsJpaRepository,
  private val todayRepository: ClickTodayStatsJpaRepository,
  private val totalCacheRepository: ClickTotalStatsCacheRedisRepository,
  private val todayCacheRepository: ClickTodayStatsCacheRedisRepository
): ClickStatsRepository {

  override fun click(amount: Long) {
    val totalClicks = incrementTotalClicks(amount)
    val todayClicks = incrementTodayClicks(amount)

    syncTotalClicksCache(totalClicks)
    syncTodayClicksCache(todayClicks)
  }

  override fun getTodayClickCount(): Long {
    return todayCacheRepository.findById(LocalDate.now().toString())
      .getOrNull()?.todayClicks ?: 0L
  }

  override fun getTotalClickCount(): Long {
    return totalCacheRepository.findById(1L)
      .getOrNull()?.totalClicks ?: 0L
  }

  private fun incrementTotalClicks(amount: Long): ClickTotalStats {
    val clickTotalStats = totalRepository.findById(1L).orElse(totalRepository.save(ClickTotalStats()))
    clickTotalStats.incrementTotalClicks(amount)

    return totalRepository.save(clickTotalStats)
  }

  private fun incrementTodayClicks(amount: Long): ClickTodayStats {
    val clickTodayStats = todayRepository.findByTodayDate(LocalDate.now()) ?: todayRepository.save(ClickTodayStats())
    clickTodayStats.incrementTodayClicks(amount)

    return todayRepository.save(clickTodayStats)
  }

  private fun syncTotalClicksCache(entity: ClickTotalStats) {
    totalCacheRepository.save(ClickTotalStatsCache(totalClicks = entity.totalClicks))
  }

  private fun syncTodayClicksCache(entity: ClickTodayStats) {
    todayCacheRepository.save(
      ClickTodayStatsCache(
        todayDate= entity.todayDate.toString(),
        todayClicks = entity.todayClicks
      )
    )
  }

}
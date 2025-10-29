package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.url.`in`.UrlClickLogger
import dev.haisia.quickurl.application.url.out.UrlClickLogRepository
import dev.haisia.quickurl.application.url.out.UrlClickStatisticsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UrlClickLogService(
  private val logRepository: UrlClickLogRepository,
  private val statisticsRepository: UrlClickStatisticsRepository,
) : UrlClickLogger {
  companion object {
    private val log = LoggerFactory.getLogger(UrlClickLogService::class.java)
  }

  @Transactional(readOnly = true)
  fun getClickCount(shortKey: String): Long {
    return logRepository.countByShortKey(shortKey)
  }

  @Transactional(readOnly = true)
  override fun getGlobalClickStats(): Pair<Long, Long> {
    return Pair(getClickTodayCount(), getClickTotalCount())
  }

  private fun getClickTodayCount(): Long {
    return statisticsRepository.getDailyClickCount()
  }

  private fun getClickTotalCount(): Long {
    return statisticsRepository.getCumulativeClickCount()
  }

}

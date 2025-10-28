package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.url.`in`.UrlClickLogger
import dev.haisia.quickurl.application.url.out.UrlClickLogRepository
import dev.haisia.quickurl.application.url.out.UrlClickStatisticsRepository
import dev.haisia.quickurl.domain.url.UrlClickLog
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UrlClickLogService(
  private val logRepository: UrlClickLogRepository,
  private val statisticsRepository: UrlClickStatisticsRepository,
) : UrlClickLogger {
  companion object {
    private val log = LoggerFactory.getLogger(UrlClickLogService::class.java)
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  override fun logClickAsync(
    shortKey: String,
    ipAddress: String?,
    userAgent: String?,
    referer: String?
  ) {
    try {
      val urlClickLog = UrlClickLog.of(
        shortKey = shortKey,
        ipAddress = ipAddress,
        userAgent = userAgent,
        referer = referer
      )
      
      logRepository.save(urlClickLog)
      statisticsRepository.click()
      
      log.debug("Click log saved - shortKey: {}, ip: {}, userAgent: {}", shortKey, ipAddress, userAgent?.take(50))
    } catch (e: Exception) {
      log.error("Failed to save click log for shortKey: {}", shortKey, e)
    }
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

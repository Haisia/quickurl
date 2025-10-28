package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.url.`in`.ClickLogger
import dev.haisia.quickurl.application.url.out.ClickLogRepository
import dev.haisia.quickurl.application.url.out.ClickStatsRepository
import dev.haisia.quickurl.domain.url.ClickLog
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ClickLogService(
  private val clickLogRepository: ClickLogRepository,
  private val clickStateRepository: ClickStatsRepository,
) : ClickLogger {
  companion object {
    private val log = LoggerFactory.getLogger(ClickLogService::class.java)
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
      val clickLog = ClickLog.of(
        shortKey = shortKey,
        ipAddress = ipAddress,
        userAgent = userAgent,
        referer = referer
      )
      
      clickLogRepository.save(clickLog)
      clickStateRepository.click()
      
      log.debug("Click log saved - shortKey: {}, ip: {}, userAgent: {}", shortKey, ipAddress, userAgent?.take(50))
    } catch (e: Exception) {
      log.error("Failed to save click log for shortKey: {}", shortKey, e)
    }
  }

  @Transactional(readOnly = true)
  fun getClickCount(shortKey: String): Long {
    return clickLogRepository.countByShortKey(shortKey)
  }

  @Transactional(readOnly = true)
  override fun getGlobalClickStats(): Pair<Long, Long> {
    return Pair(getClickTodayCount(), getClickTotalCount())
  }

  private fun getClickTodayCount(): Long {
    return clickStateRepository.getTodayClickCount()
  }

  private fun getClickTotalCount(): Long {
    return clickStateRepository.getTotalClickCount()
  }

}

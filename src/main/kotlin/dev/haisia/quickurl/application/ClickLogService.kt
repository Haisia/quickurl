package dev.haisia.quickurl.application

import dev.haisia.quickurl.application.`in`.ClickLogger
import dev.haisia.quickurl.application.out.ClickLogRepository
import dev.haisia.quickurl.domain.ClickLog
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ClickLogService(
  private val clickLogRepository: ClickLogRepository
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
      
      log.debug(
        "Click log saved - shortKey: {}, ip: {}, userAgent: {}", 
        shortKey, 
        ipAddress, 
        userAgent?.take(50)
      )
    } catch (e: Exception) {
      log.error("Failed to save click log for shortKey: {}", shortKey, e)
    }
  }

  @Transactional(readOnly = true)
  fun getClickCount(shortKey: String): Long {
    return clickLogRepository.countByShortKey(shortKey)
  }
}

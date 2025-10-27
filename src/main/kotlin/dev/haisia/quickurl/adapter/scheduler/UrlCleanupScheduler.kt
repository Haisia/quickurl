package dev.haisia.quickurl.adapter.scheduler

import dev.haisia.quickurl.application.`in`.UrlCleaner
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UrlCleanupScheduler(
  private val urlCleaner: UrlCleaner
) {
  
  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * 매일 새벽 2시에 3개월 이상 사용하지 않은 URL 삭제
   * Cron: 초 분 시 일 월 요일
   */
  @Scheduled(cron = "0 0 2 * * *")
  fun cleanupUnusedUrls() {
    logger.info("Starting scheduled URL cleanup job")
    
    try {
      val deletedCount = urlCleaner.deleteUnusedUrls(thresholdMonths = 3)
      logger.info("Scheduled URL cleanup completed. Deleted {} URLs", deletedCount)
    } catch (e: Exception) {
      logger.error("Failed to execute scheduled URL cleanup", e)
    }
  }
}

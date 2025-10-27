package dev.haisia.quickurl.adapter.scheduler

import dev.haisia.quickurl.application.out.ClickLogRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
* 오래된 클릭 로그를 정리하는 스케줄러
* 매일 새벽 3시에 실행되어 6개월 이상 된 로그를 삭제합니다.
*/
@Component
class ClickLogCleanupScheduler(
  private val clickLogRepository: ClickLogRepository
) {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
  * 매일 새벽 3시에 실행 (cron: 초 분 시 일 월 요일)
  */
  @Scheduled(cron = "0 0 3 * * *")
  @Transactional
  fun cleanupOldClickLogs() {
    val thresholdMonths = 6L
    val threshold = LocalDateTime.now().minusMonths(thresholdMonths)

    logger.info("Starting click log cleanup - deleting logs before: {}", threshold)

    try {
      val deletedCount = clickLogRepository.deleteByClickedAtBefore(threshold)
      logger.info(
        "Click log cleanup completed - deleted {} logs (older than {} months)",
        deletedCount,
        thresholdMonths
      )
    } catch (e: Exception) {
      logger.error("Failed to cleanup old click logs", e)
    }
  }
}

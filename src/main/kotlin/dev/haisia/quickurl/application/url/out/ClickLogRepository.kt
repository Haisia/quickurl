package dev.haisia.quickurl.application.url.out

import dev.haisia.quickurl.domain.url.ClickLog
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ClickLogRepository : JpaRepository<ClickLog, Long> {
  /**
   * 특정 shortKey에 대한 클릭 수를 조회합니다.
   */
  fun countByShortKey(shortKey: String): Long

  /**
   * 특정 기간 동안의 클릭 로그를 조회합니다.
   */
  fun findByShortKeyAndClickedAtBetween(
    shortKey: String,
    startDate: LocalDateTime,
    endDate: LocalDateTime
  ): List<ClickLog>

  /**
   * 오래된 로그를 삭제합니다.
   */
  fun deleteByClickedAtBefore(threshold: LocalDateTime): Int
}

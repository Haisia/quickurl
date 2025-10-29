package dev.haisia.quickurl.application.url.out

interface UrlClickStatisticsRepository {
  fun click(amount: Long = 1L)
  fun getDailyClickCount(): Long
  fun getCumulativeClickCount(): Long
}

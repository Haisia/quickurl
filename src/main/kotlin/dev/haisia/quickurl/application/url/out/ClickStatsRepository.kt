package dev.haisia.quickurl.application.url.out

interface ClickStatsRepository {
  fun click(amount: Long = 1L)
  fun getTodayClickCount(): Long
  fun getTotalClickCount(): Long
}
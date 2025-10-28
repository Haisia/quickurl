package dev.haisia.quickurl.adapter.persistence.urlclickstatistics

import java.time.LocalDate

sealed class UrlClickStatisticsCacheSyncEvent {
  data class CumulativeClicksUpdated(val cumulativeClicks: Long) : UrlClickStatisticsCacheSyncEvent()
  data class DailyClicksUpdated(val date: LocalDate, val dailyClicks: Long) : UrlClickStatisticsCacheSyncEvent()
}

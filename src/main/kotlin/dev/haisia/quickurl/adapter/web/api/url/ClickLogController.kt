package dev.haisia.quickurl.adapter.web.api.url

import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.ClickStatsResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetUrlClickStatsResponse
import dev.haisia.quickurl.application.url.UrlClickLogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1")
@RestController
class ClickLogController(
  private val urlClickLogService: UrlClickLogService
) {

  @GetMapping("/stats/{shortKey}")
  fun getClickStats(@PathVariable shortKey: String): ResponseEntity<ApiResponse<ClickStatsResponse>> {
    val clickCount = urlClickLogService.getClickCount(shortKey)

    return ApiResponse.ok(
      ClickStatsResponse(
        shortKey = shortKey,
        totalClicks = clickCount
      )
    )
  }

  @GetMapping("/stats/global")
  fun getGlobalStats(): ResponseEntity<ApiResponse<GetUrlClickStatsResponse>> {
    val (daily, cumulative) = urlClickLogService.getGlobalClickStats()
    return ApiResponse.ok(
      GetUrlClickStatsResponse(
        dailyClickCount = daily,
        cumulativeClickCount = cumulative
      )
    )
  }
}

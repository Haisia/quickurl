package dev.haisia.quickurl.adapter.web.api.url

import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.ClickStatsResponse
import dev.haisia.quickurl.application.url.ClickLogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/stats")
@RestController
class ClickLogController(
  private val clickLogService: ClickLogService
) {

  @GetMapping("/{shortKey}")
  fun getClickStats(@PathVariable shortKey: String): ResponseEntity<ApiResponse<ClickStatsResponse>> {
    val clickCount = clickLogService.getClickCount(shortKey)

    return ApiResponse.Companion.ok(
      ClickStatsResponse(
        shortKey = shortKey,
        totalClicks = clickCount
      )
    )
  }
}

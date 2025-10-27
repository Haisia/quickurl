package dev.haisia.quickurl.adapter.webapi

import dev.haisia.quickurl.adapter.webapi.dto.ApiResponse
import dev.haisia.quickurl.adapter.webapi.dto.ClickStatsResponse
import dev.haisia.quickurl.application.ClickLogService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/stats")
class ClickLogController(
  private val clickLogService: ClickLogService
) {

  @GetMapping("/{shortKey}")
  fun getClickStats(@PathVariable shortKey: String): ResponseEntity<ApiResponse<ClickStatsResponse>> {
    val clickCount = clickLogService.getClickCount(shortKey)

    return ApiResponse.ok(
      ClickStatsResponse(
        shortKey = shortKey,
        totalClicks = clickCount
      )
    )
  }
}

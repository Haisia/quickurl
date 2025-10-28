package dev.haisia.quickurl.adapter.web.api.url

import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlRequest
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlResponse
import dev.haisia.quickurl.application.`in`.UrlCreator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1")
@RestController
class UrlApiController(
  private val urlCreator: UrlCreator,
) {
  @PostMapping("/shorten")
  fun createUrl(
    @RequestBody request: CreateUrlRequest
  ): ResponseEntity<ApiResponse<CreateUrlResponse>> {
    val shortKey = urlCreator.createShortKey(request.originalUrl)
    return ApiResponse.created(CreateUrlResponse(shortKey))
  }
}
package dev.haisia.quickurl.adapter.web.api.url

import dev.haisia.quickurl.adapter.web.api.ApiEmptyData
import dev.haisia.quickurl.adapter.web.api.ApiPageableData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlRequest
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetMyUrlsResponse
import dev.haisia.quickurl.application.`in`.UrlCreator
import dev.haisia.quickurl.application.url.`in`.UrlCleaner
import dev.haisia.quickurl.application.url.`in`.UrlFinder
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1")
@RestController
class UrlController(
  private val urlCreator: UrlCreator,
  private val urlFinder: UrlFinder,
  private val urlCleaner: UrlCleaner,
) {

  @PostMapping("/url/shorten")
  fun createUrl(
    @RequestBody request: CreateUrlRequest
  ): ResponseEntity<ApiResponse<CreateUrlResponse>> {
    val shortKey = urlCreator.createShortKey(request.originalUrl)
    return ApiResponse.created(CreateUrlResponse(shortKey))
  }

  @DeleteMapping("/url/shorten/{shortKey}")
  fun deleteUrl(@PathVariable shortKey: String): ResponseEntity<ApiResponse<ApiEmptyData>> {
    urlCleaner.deleteMyUrl(shortKey)
    return ApiResponse.ok(ApiEmptyData())
  }

  @GetMapping("/urls/me")
  fun getMyUrls(
    @PageableDefault(size = 20, sort = ["createdAt"], direction = DESC) pageable: Pageable
  ): ResponseEntity<ApiResponse<ApiPageableData<GetMyUrlsResponse>>> {
    val page = urlFinder.findMyUrls(pageable).map { GetMyUrlsResponse.from(it) }
    return ApiResponse.ok(ApiPageableData.from(page))
  }
}
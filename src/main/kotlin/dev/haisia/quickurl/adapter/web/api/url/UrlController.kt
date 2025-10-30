package dev.haisia.quickurl.adapter.web.api.url

import dev.haisia.quickurl.adapter.config.AppProperties
import dev.haisia.quickurl.adapter.web.api.ApiEmptyData
import dev.haisia.quickurl.adapter.web.api.ApiPageableData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlRequest
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetMyUrlsResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetUrlQrCodeResponse
import dev.haisia.quickurl.application.shared.out.QrCodeHandler
import dev.haisia.quickurl.application.url.`in`.UrlCleaner
import dev.haisia.quickurl.application.url.`in`.UrlCreator
import dev.haisia.quickurl.application.url.`in`.UrlFinder
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1")
@RestController
class UrlController(
  private val urlCreator: UrlCreator,
  private val urlFinder: UrlFinder,
  private val urlCleaner: UrlCleaner,
  private val qrCodeHandler: QrCodeHandler,
  private val appProperties: AppProperties,
) {

  @PostMapping("/url/shorten")
  fun createUrl(
    @RequestBody request: CreateUrlRequest
  ): ResponseEntity<ApiResponse<CreateUrlResponse>> {
    val shortKey = urlCreator.createShortKey(request.originalUrl, request.expirationDuration)
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

  @GetMapping("/url/qr-code")
  fun getUrlQrCode(
    @RequestParam shortKey: String,
    @RequestParam(defaultValue = "200") size: Int
  ): ResponseEntity<ApiResponse<GetUrlQrCodeResponse>> {
    // shortKey 검증: Base62 문자만 허용 (a-z, A-Z, 0-9, 하이픈)
    val validPattern = Regex("^[a-zA-Z0-9-]+$")
    if (!validPattern.matches(shortKey)) {
      throw IllegalArgumentException("Invalid shortKey format")
    }
    
    val url = "${appProperties.baseUrl}/$shortKey"

    val qrCodeImage: ByteArray = qrCodeHandler.generateQrCodeImage(url, size, size)
    
    // Base64로 인코딩하여 JSON으로 반환
    val base64Image = java.util.Base64.getEncoder().encodeToString(qrCodeImage)
    
    return ApiResponse.ok(GetUrlQrCodeResponse(base64Image))
  }
}
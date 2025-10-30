package dev.haisia.quickurl.adapter.web.api.url.docs

import dev.haisia.quickurl.adapter.web.api.ApiEmptyData
import dev.haisia.quickurl.adapter.web.api.ApiPageableData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlRequest
import dev.haisia.quickurl.adapter.web.api.url.dto.CreateUrlResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetMyUrlsResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetUrlQrCodeResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity

@Tag(name = "URL 단축", description = "URL 단축 및 관리 API")
interface UrlApiDocs {

  @Operation(
    summary = "URL 단축",
    description = "긴 URL을 짧은 URL로 변환합니다. 선택적으로 만료 기간을 설정할 수 있습니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "201",
        description = "URL 단축 성공",
        content = [Content(schema = Schema(implementation = CreateUrlResponse::class))]
      ),
      SwaggerApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (URL 형식 오류 등)"
      ),
      SwaggerApiResponse(
        responseCode = "401",
        description = "인증 필요"
      )
    ]
  )
  fun createUrl(
    @RequestBody(
      description = "URL 단축 요청",
      required = true,
      content = [Content(schema = Schema(implementation = CreateUrlRequest::class))]
    )
    request: CreateUrlRequest
  ): ResponseEntity<ApiResponse<CreateUrlResponse>>

  @Operation(
    summary = "단축 URL 삭제",
    description = "자신이 생성한 단축 URL을 삭제합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "삭제 성공"
      ),
      SwaggerApiResponse(
        responseCode = "401",
        description = "인증 필요"
      ),
      SwaggerApiResponse(
        responseCode = "403",
        description = "권한 없음 (본인이 생성한 URL이 아님)"
      ),
      SwaggerApiResponse(
        responseCode = "404",
        description = "존재하지 않는 단축 URL"
      )
    ]
  )
  fun deleteUrl(
    @Parameter(
      description = "단축 URL 키",
      example = "abc123",
      required = true
    )
    shortKey: String
  ): ResponseEntity<ApiResponse<ApiEmptyData>>

  @Operation(
    summary = "내가 생성한 URL 목록 조회",
    description = "현재 로그인한 사용자가 생성한 단축 URL 목록을 페이지네이션하여 조회합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = GetMyUrlsResponse::class))]
      ),
      SwaggerApiResponse(
        responseCode = "401",
        description = "인증 필요"
      )
    ]
  )
  fun getMyUrls(
    @Parameter(
      description = "페이지네이션 정보",
      schema = Schema(implementation = Pageable::class)
    )
    pageable: Pageable
  ): ResponseEntity<ApiResponse<ApiPageableData<GetMyUrlsResponse>>>

  @Operation(
    summary = "단축 URL QR 코드 생성",
    description = "단축 URL에 대한 QR 코드를 생성하여 Base64 인코딩된 이미지로 반환합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "QR 코드 생성 성공",
        content = [Content(schema = Schema(implementation = GetUrlQrCodeResponse::class))]
      ),
      SwaggerApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (shortKey 형식 오류 등)"
      )
    ]
  )
  fun getUrlQrCode(
    @Parameter(
      description = "단축 URL 키",
      example = "abc123",
      required = true
    )
    shortKey: String,
    @Parameter(
      description = "QR 코드 이미지 크기 (픽셀)",
      example = "200"
    )
    size: Int
  ): ResponseEntity<ApiResponse<GetUrlQrCodeResponse>>
}

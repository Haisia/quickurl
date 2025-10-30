package dev.haisia.quickurl.adapter.web.api.url.docs

import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.ClickStatsResponse
import dev.haisia.quickurl.adapter.web.api.url.dto.GetUrlClickStatsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "클릭 통계", description = "URL 클릭 통계 조회 API")
interface ClickLogApiDocs {

  @Operation(
    summary = "단축 URL 클릭 통계 조회",
    description = "특정 단축 URL의 총 클릭 수를 조회합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = ClickStatsResponse::class))]
      ),
      SwaggerApiResponse(
        responseCode = "404",
        description = "존재하지 않는 단축 URL"
      )
    ]
  )
  fun getClickStats(
    @Parameter(
      description = "단축 URL 키",
      example = "abc123",
      required = true
    )
    shortKey: String
  ): ResponseEntity<ApiResponse<ClickStatsResponse>>

  @Operation(
    summary = "전체 클릭 통계 조회",
    description = "전체 시스템의 일일 클릭 수와 누적 클릭 수를 조회합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = GetUrlClickStatsResponse::class))]
      )
    ]
  )
  fun getGlobalStats(): ResponseEntity<ApiResponse<GetUrlClickStatsResponse>>
}

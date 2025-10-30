package dev.haisia.quickurl.adapter.web.api.auth.docs

import dev.haisia.quickurl.adapter.security.CustomUserDetails
import dev.haisia.quickurl.adapter.web.api.ApiEmptyData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserInfoResponse
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserLoginRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserRegisterRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserRegisterResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.CookieValue

@Tag(name = "인증", description = "사용자 인증 관련 API")
interface AuthApiDocs {

  @Operation(
    summary = "회원가입",
    description = "새로운 사용자를 등록합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "201",
        description = "회원가입 성공",
        content = [Content(schema = Schema(implementation = UserRegisterResponse::class))]
      ),
      SwaggerApiResponse(
        responseCode = "400",
        description = "잘못된 요청 (이메일 형식 오류, 비밀번호 규칙 위반 등)"
      ),
      SwaggerApiResponse(
        responseCode = "409",
        description = "이미 존재하는 이메일"
      )
    ]
  )
  fun register(
    @RequestBody(
      description = "회원가입 정보",
      required = true,
      content = [Content(schema = Schema(implementation = UserRegisterRequest::class))]
    )
    request: UserRegisterRequest
  ): ResponseEntity<ApiResponse<UserRegisterResponse>>

  @Operation(
    summary = "로그인",
    description = "이메일과 비밀번호로 로그인합니다. 성공 시 액세스 토큰과 리프레시 토큰이 쿠키에 저장됩니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "로그인 성공"
      ),
      SwaggerApiResponse(
        responseCode = "401",
        description = "인증 실패 (잘못된 이메일 또는 비밀번호)"
      )
    ]
  )
  fun login(
    @RequestBody(
      description = "로그인 정보",
      required = true,
      content = [Content(schema = Schema(implementation = UserLoginRequest::class))]
    )
    request: UserLoginRequest,
    response: HttpServletResponse
  ): ResponseEntity<ApiResponse<ApiEmptyData>>

  @Operation(
    summary = "현재 사용자 정보 조회",
    description = "현재 로그인한 사용자의 정보를 조회합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = UserInfoResponse::class))]
      )
    ]
  )
  fun getCurrentUser(
    @AuthenticationPrincipal userDetails: CustomUserDetails?
  ): ResponseEntity<ApiResponse<UserInfoResponse>>

  @Operation(
    summary = "로그아웃",
    description = "현재 사용자를 로그아웃합니다. 리프레시 토큰을 만료시키고 쿠키를 삭제합니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "로그아웃 성공"
      )
    ]
  )
  fun logout(
    @CookieValue(name = "refresh_token", required = false) refreshToken: String?,
    response: HttpServletResponse
  ): ResponseEntity<ApiResponse<ApiEmptyData>>

  @Operation(
    summary = "액세스 토큰 갱신",
    description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
  )
  @ApiResponses(
    value = [
      SwaggerApiResponse(
        responseCode = "200",
        description = "토큰 갱신 성공"
      ),
      SwaggerApiResponse(
        responseCode = "401",
        description = "유효하지 않은 리프레시 토큰"
      )
    ]
  )
  fun refreshToken(
    @CookieValue(name = "refresh_token", required = true) refreshToken: String,
    response: HttpServletResponse
  ): ResponseEntity<ApiResponse<ApiEmptyData>>
}

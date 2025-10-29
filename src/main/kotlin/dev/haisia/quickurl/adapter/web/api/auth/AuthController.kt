package dev.haisia.quickurl.adapter.web.api.auth

import dev.haisia.quickurl.adapter.security.CustomUserDetails
import dev.haisia.quickurl.adapter.web.api.ApiEmptyData
import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.CookieUtils
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserInfoResponse
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserLoginRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserRegisterRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserRegisterResponse
import dev.haisia.quickurl.application.user.`in`.UserAuthorizer
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/auth")
@RestController
class AuthController(
  private val userAuthorizer: UserAuthorizer,
  private val cookieUtils: CookieUtils
) {

  @PostMapping("/register")
  fun register(@RequestBody request: UserRegisterRequest): ResponseEntity<ApiResponse<UserRegisterResponse>> {

    val user = userAuthorizer.createUser(request.email, request.password)

    return ApiResponse.created(UserRegisterResponse(user.id.toString(), user.email.value))
  }

  @PostMapping("/login")
  fun login(
    @RequestBody request: UserLoginRequest,
    response: HttpServletResponse
  ): ResponseEntity<ApiResponse<ApiEmptyData>> {

    val (accessToken, refreshToken) = userAuthorizer.loginUser(request.email, request.password)

    // 쿠키에 토큰 저장
    response.addHeader("Set-Cookie", cookieUtils.createAccessTokenCookie(accessToken).toString())
    response.addHeader("Set-Cookie", cookieUtils.createRefreshTokenCookie(refreshToken).toString())

    return ApiResponse.ok(ApiEmptyData())
  }

  @GetMapping("/me")
  fun getCurrentUser(
    @AuthenticationPrincipal userDetails: CustomUserDetails?
  ): ResponseEntity<ApiResponse<UserInfoResponse>> {
    return when {
      userDetails != null -> ApiResponse.ok(UserInfoResponse(email = userDetails.email.value, isLoggedIn = true))
      else -> ApiResponse.ok(UserInfoResponse(email = "", isLoggedIn = false))
    }
  }

  @PostMapping("/logout")
  fun logout(
    @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE_NAME, required = false) refreshToken: String?,
    response: HttpServletResponse
  ): ResponseEntity<ApiResponse<ApiEmptyData>> {

    refreshToken?.let { userAuthorizer.expireRefreshToken(it) }

    response.addHeader("Set-Cookie", cookieUtils.createExpiredAccessTokenCookie().toString())
    response.addHeader("Set-Cookie", cookieUtils.createExpiredRefreshTokenCookie().toString())

    return ApiResponse.ok(ApiEmptyData())
  }

  @PostMapping("/token/refresh")
  fun refreshToken(
    @CookieValue(name = CookieUtils.REFRESH_TOKEN_COOKIE_NAME, required = true) refreshToken: String,
    response: HttpServletResponse
  ): ResponseEntity<ApiResponse<ApiEmptyData>> {

    val accessToken = userAuthorizer.accessTokenRefresh(refreshToken)

    response.addHeader("Set-Cookie", cookieUtils.createAccessTokenCookie(accessToken).toString())

    return ApiResponse.ok(ApiEmptyData())
  }
}

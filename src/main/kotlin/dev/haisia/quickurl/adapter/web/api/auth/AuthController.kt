package dev.haisia.quickurl.adapter.web.api.auth

import dev.haisia.quickurl.adapter.web.api.ApiResponse
import dev.haisia.quickurl.adapter.web.api.auth.dto.CreateAccessTokenByRefreshTokenRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.CreateAccessTokenByRefreshTokenResponse
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserLoginRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserLoginResponse
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserRegisterRequest
import dev.haisia.quickurl.adapter.web.api.auth.dto.UserRegisterResponse
import dev.haisia.quickurl.application.user.`in`.UserAuthorizer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/auth")
@RestController
class AuthController(
  private val userAuthorizer: UserAuthorizer
) {

  @PostMapping("/register")
  fun register(@RequestBody request: UserRegisterRequest): ResponseEntity<ApiResponse<UserRegisterResponse>> {

    val user = userAuthorizer.createUser(request.email, request.password)

    return ApiResponse.Companion.created(UserRegisterResponse(user.id.toString(), user.email.value))
  }

  @PostMapping("/login")
  fun login(@RequestBody request: UserLoginRequest): ResponseEntity<ApiResponse<UserLoginResponse>> {

    val (accessToken, refreshToken) = userAuthorizer.loginUser(request.email, request.password)

    return ApiResponse.Companion.ok(
      UserLoginResponse(
        accessToken = accessToken,
        refreshToken = refreshToken
      )
    )
  }

  @PostMapping("/accesstoken")
  fun createAccessTokenByRefreshToken(@RequestBody request: CreateAccessTokenByRefreshTokenRequest): ResponseEntity<ApiResponse<CreateAccessTokenByRefreshTokenResponse>> {
    val accessToken = userAuthorizer.accessTokenRefresh(request.refreshToken)

    return ApiResponse.Companion.ok(CreateAccessTokenByRefreshTokenResponse(accessToken))
  }
}
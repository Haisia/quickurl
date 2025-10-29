package dev.haisia.quickurl.application.user.`in`

import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.user.User

interface UserAuthorizer {
  fun createUser(email: Email, password: Password): User
  /* Pair<accessToken, refreshToken> */
  fun loginUser(email: Email, password: Password): Pair<String, String>
  /* refresh 토큰을 통해 엑세스 토큰을 재발급합니다. */
  fun accessTokenRefresh(refreshToken: String): String

  fun expireRefreshToken(refreshToken: String)
}
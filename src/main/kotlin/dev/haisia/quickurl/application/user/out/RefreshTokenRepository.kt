package dev.haisia.quickurl.application.user.out

import dev.haisia.quickurl.domain.user.User
import java.util.*

interface RefreshTokenRepository {
  /**
  * 리프레시 토큰 저장
  * - 해당 유저의 활성화된 토큰이 있다면 비활성화 처리
  */
  fun save(refreshToken: String, user: User): String

  /**
  * 사용자 ID로 활성화된 리프레시 토큰 조회
  */
  fun findEnabledByUserId(userId: UUID): String?

  fun increaseIssueCount(refreshToken: String): Boolean

  fun disableRefreshToken(token: String): Int
}
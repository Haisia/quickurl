package dev.haisia.quickurl.adapter.persistence.refreshtoken

import dev.haisia.quickurl.application.user.out.RefreshTokenRepository
import dev.haisia.quickurl.domain.user.User
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
class RefreshTokenRepositoryImpl(
  private val refreshTokenJpaRepository: RefreshTokenJpaRepository
) : RefreshTokenRepository {

  @Transactional
  override fun save(refreshToken: String, user: User): String {
    // 해당 유저의 활성화된 토큰이 있다면 비활성화 처리
    val userId = user.getIdOrThrow()

    return RefreshToken.of(refreshToken, user).let {
      refreshTokenJpaRepository.disableAllActiveTokensByUserId(userId)
      refreshTokenJpaRepository.save(it)
      it.token
    }
  }

  @Transactional(readOnly = true)
  override fun findEnabledByUserId(userId: UUID): String? {
    return refreshTokenJpaRepository.findByUserIdAndEnabled(userId, true)?.token
  }

  @Transactional
  override fun increaseIssueCount(refreshToken: String): Boolean {
    return refreshTokenJpaRepository.increaseIssueCount(refreshToken) > 0
  }

  @Transactional
  override fun disableRefreshToken(token: String): Int {
    return refreshTokenJpaRepository.disableRefreshToken(token)
  }

}
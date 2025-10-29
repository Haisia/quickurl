package dev.haisia.quickurl.adapter.persistence.refreshtoken

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface RefreshTokenJpaRepository : JpaRepository<RefreshToken, Long> {

  /**
  * 사용자 ID와 활성화 상태로 리프레시 토큰 조회
  */
  fun findByUserIdAndEnabled(userId: UUID, enabled: Boolean): RefreshToken?


  /**
  * 사용자의 모든 활성화된 토큰을 비활성화 처리
  */
  @Modifying
  @Query(
    """
    update RefreshToken rt 
    set rt.enabled = false 
    where rt.user.id = :userId 
      and rt.enabled = true
    """)
  fun disableAllActiveTokensByUserId(@Param("userId") userId: UUID): Int

  @Modifying
  @Query(
    """
    update RefreshToken rt 
    set rt.issueCount = rt.issueCount + 1 
    where rt.token = :token
    """)
  fun increaseIssueCount(token: String): Int

  @Modifying
  @Query(
    """
    update RefreshToken rt 
    set rt.enabled = false 
    where rt.token = :token
    """
  )
  fun disableRefreshToken(token: String): Int
}

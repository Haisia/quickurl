package dev.haisia.quickurl.adapter.persistence.refreshtoken

import dev.haisia.quickurl.domain.user.User
import jakarta.persistence.*
import java.time.LocalDateTime

/*
* 특별한 비지니스로직 없이
* 단순히 리프레시토큰 보관 용도로 사용하기 때문에
* 도메인이 아닌 엔티티로 설계 함
* */
@Table(name = "refresh_tokens")
@Entity
class RefreshToken(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  val id: Long? = null,

  @Column(name = "token", nullable = false, unique = true)
  val token: String,

  @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  val user: User,

  /* accessToken 발급 횟수 */
  @Column(name = "issue_count", nullable = false)
  var issueCount: Long = 0,

  /* 강제 비활성화 */
  @Column(name = "enabled", nullable = false)
  var enabled: Boolean = true,

  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),
) {
  companion object {
    fun of(token: String, user: User): RefreshToken = RefreshToken(token = token, user = user)
  }
}
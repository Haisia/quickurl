package dev.haisia.quickurl.adapter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 보안 관련 프로퍼티 설정
 */
@Component
@ConfigurationProperties(prefix = "app.security")
data class SecurityProperties(
  /**
   * Referer 검증 관련 설정
   */
  val refererCheck: RefererCheckConfig = RefererCheckConfig()
) {
  
  data class RefererCheckConfig(
    /**
     * Referer 검증 활성화 여부
     * true: Referer 검증 활성화 (외부 직접 API 호출 차단)
     * false: Referer 검증 비활성화 (모든 API 호출 허용)
     */
    var enabled: Boolean = false,
    
    /**
     * 허용할 도메인 목록
     * 예: ["http://localhost:8080", "https://mydomain.com"]
     */
    var allowedDomains: List<String> = listOf(),
    
    /**
     * 서브도메인 허용 여부
     * true: *.example.com 형태의 서브도메인 허용
     * false: 정확히 일치하는 도메인만 허용
     */
    var allowSubdomains: Boolean = false,
    
    /**
     * Referer 검증을 수행할 API 경로 패턴
     * 이 경로들은 Referer 검증 대상이 됩니다.
     */
    var protectedPaths: List<String> = listOf(),
    
    /**
     * Referer 검증에서 제외할 경로 패턴
     * 이 경로들은 Referer 검증을 하지 않습니다.
     */
    var excludedPaths: List<String> = listOf()
  )
}

package dev.haisia.quickurl.adapter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

  /**
  * Rate Limit 설정 Properties
  *
  * application.yaml에서 설정을 외부화할 수 있습니다.
  */
@Component
@ConfigurationProperties(prefix = "rate-limit")
data class RateLimitProperties(
  /**
  * Rate Limit 활성화 여부
  */
  var enabled: Boolean = true,

  /**
  * 기간당 최대 요청 수
  */
  var requestLimit: Long = 10,

  /**
  * 리필 기간 (초)
  */
  var refillDurationSeconds: Long = 1,

  /**
  * Rate Limit 제외 경로
  */
  var excludedPaths: List<String> = listOf(
    "/favicon.ico",
    "/actuator",
    "/error"
  )
) {
  fun getRefillDuration(): Duration = Duration.ofSeconds(refillDurationSeconds)
}

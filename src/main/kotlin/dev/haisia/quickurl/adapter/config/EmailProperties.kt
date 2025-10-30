package dev.haisia.quickurl.adapter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.email")
class EmailProperties(
  val send: SendConfig = SendConfig()
) {
  data class SendConfig(
    var enabled: Boolean = false,
  )
}
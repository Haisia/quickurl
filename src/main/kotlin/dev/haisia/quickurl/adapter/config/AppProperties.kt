package dev.haisia.quickurl.adapter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app")
data class AppProperties(
  var baseUrl: String = ""
)

package dev.haisia.quickurl.adapter.web.api.url.dto

import dev.haisia.quickurl.domain.url.OriginalUrl

data class CreateUrlRequest(
  val originalUrl: OriginalUrl
)

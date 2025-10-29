package dev.haisia.quickurl.application.url.`in`

import dev.haisia.quickurl.application.url.dto.UrlClickDto
import dev.haisia.quickurl.domain.url.OriginalUrl

interface UrlClicker {
  fun click(urlClickDto : UrlClickDto): OriginalUrl
}
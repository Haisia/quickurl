package dev.haisia.quickurl.application.url.`in`

import dev.haisia.quickurl.application.url.dto.UrlClickDto

interface UrlClicker {
  /* @return: originalUrl */
  fun click(urlClickDto : UrlClickDto): String
}
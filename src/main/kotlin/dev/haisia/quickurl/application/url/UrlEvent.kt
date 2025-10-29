package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.url.dto.UrlClickDto

sealed class UrlEvent {
  data class UrlCreated(val urlId: Long) : UrlEvent()
  data class UrlDeleted(val shortKey: String) : UrlEvent()
  data class UrlClicked(val urlClickDto: UrlClickDto) : UrlEvent()
}
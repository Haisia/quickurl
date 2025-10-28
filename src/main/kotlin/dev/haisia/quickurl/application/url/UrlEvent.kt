package dev.haisia.quickurl.application.url

sealed class UrlEvent {
  data class UrlDeleted(val shortKey: String) : UrlEvent()
}
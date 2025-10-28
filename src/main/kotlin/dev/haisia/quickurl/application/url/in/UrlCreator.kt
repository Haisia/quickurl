package dev.haisia.quickurl.application.url.`in`

interface UrlCreator {
  fun createShortKey(originalUrl: String): String
}
package dev.haisia.quickurl.application.`in`

interface UrlCreator {
  fun createShortKey(originalUrl: String): String
}
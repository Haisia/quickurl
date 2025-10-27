package dev.haisia.quickurl.application.`in`

interface UrlCreator {
  fun createShortUrl(originalUrl: String): String
}
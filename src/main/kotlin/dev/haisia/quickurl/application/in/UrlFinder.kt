package dev.haisia.quickurl.application.`in`

interface UrlFinder {
  fun findOriginalUrl(shortKey: String): String
}
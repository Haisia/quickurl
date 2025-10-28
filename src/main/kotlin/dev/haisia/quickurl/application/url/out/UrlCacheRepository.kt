package dev.haisia.quickurl.application.url.out

interface UrlCacheRepository {
  fun get(shortKey: String): String?
  fun set(shortKey: String, originalUrl: String)
  fun delete(shortKey: String)
}

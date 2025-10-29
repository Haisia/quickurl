package dev.haisia.quickurl.application.url.out

import dev.haisia.quickurl.domain.url.OriginalUrl

interface UrlCacheRepository {
  fun get(shortKey: String): String?
  fun set(shortKey: String, originalUrl: OriginalUrl)
  fun delete(shortKey: String)
}

package dev.haisia.quickurl.application.url.`in`

import dev.haisia.quickurl.domain.url.OriginalUrl

interface UrlCreator {
  fun createShortKey(originalUrl: OriginalUrl): String
}
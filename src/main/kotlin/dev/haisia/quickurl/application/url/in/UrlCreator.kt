package dev.haisia.quickurl.application.url.`in`

import dev.haisia.quickurl.domain.Duration
import dev.haisia.quickurl.domain.url.OriginalUrl

interface UrlCreator {
  fun createShortKey(originalUrl: OriginalUrl, expirationDuration: Duration = Duration.NONE): String
}
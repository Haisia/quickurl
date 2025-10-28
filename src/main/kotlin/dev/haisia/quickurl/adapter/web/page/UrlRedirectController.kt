package dev.haisia.quickurl.adapter.web.page

import dev.haisia.quickurl.application.url.`in`.ClickLogger
import dev.haisia.quickurl.application.url.`in`.UrlFinder
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.net.URI
import java.util.concurrent.TimeUnit

@Controller
class UrlRedirectController(
  private val urlFinder: UrlFinder,
  private val clickLogger: ClickLogger,
) {

  @GetMapping("/{shortKey}")
  fun clickUrl(
    @PathVariable shortKey: String,
    request: HttpServletRequest
  ): ResponseEntity<Unit> {
    val url = urlFinder.findOriginalUrl(shortKey)

    /* 비동기로 로그 수집 */
    clickLogger.logClickAsync(
      shortKey = shortKey,
      ipAddress = request.remoteAddr,
      userAgent = request.getHeader("User-Agent"),
      referer = request.getHeader("Referer")
    )

    return ResponseEntity
      .status(HttpStatus.MOVED_PERMANENTLY)
      .location(URI.create(url))
      .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
      .build()
  }
}
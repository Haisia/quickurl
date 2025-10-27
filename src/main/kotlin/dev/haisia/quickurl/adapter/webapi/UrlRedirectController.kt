package dev.haisia.quickurl.adapter.webapi

import dev.haisia.quickurl.application.`in`.UrlFinder
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
) {

  @GetMapping("/{shortKey}")
  fun clickUrl(
    @PathVariable shortKey: String,
    request: HttpServletRequest
  ): ResponseEntity<Unit> {
    val url = urlFinder.findOriginalUrl(shortKey)

    return ResponseEntity
      .status(HttpStatus.MOVED_PERMANENTLY)
      .location(URI.create(url))
      .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
      .build()
  }
}

package dev.haisia.quickurl.adapter.web.page

import dev.haisia.quickurl.application.url.dto.UrlClickDto
import dev.haisia.quickurl.application.url.`in`.UrlClicker
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.CacheControl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.net.URI

@Controller
class UrlRedirectPageController(
  private val urlClicker: UrlClicker,
) {

  @GetMapping("/{shortKey}")
  fun clickUrl(
    @PathVariable shortKey: String,
    request: HttpServletRequest
  ): ResponseEntity<Unit> {

    val originalUrl = urlClicker.click(
      UrlClickDto(
        shortKey = shortKey,
        ipAddress = request.remoteAddr,
        userAgent = request.getHeader("User-Agent"),
        referer = request.getHeader("Referer")
      )
    )

    return ResponseEntity
      .status(HttpStatus.FOUND)
      .location(URI.create(originalUrl))
      .cacheControl(CacheControl.noStore())
      .build()
  }
}
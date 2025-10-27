package dev.haisia.quickurl.adapter.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class FaviconFilter : OncePerRequestFilter() {
  companion object {
    private val log = LoggerFactory.getLogger(FaviconFilter::class.java)
  }

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    val requestURI = request.requestURI

    if (requestURI == "/favicon.ico") {
      handleFaviconRequest(response)
      return
    }

    filterChain.doFilter(request, response)
  }

  private fun handleFaviconRequest(response: HttpServletResponse) {
    try {
      val faviconResource = ClassPathResource("static/favicon.ico")

      if (faviconResource.exists()) {
        response.contentType = "image/x-icon"
        response.status = HttpServletResponse.SC_OK
        response.setHeader("Cache-Control", "public, max-age=86400")

        faviconResource.inputStream.use { inputStream ->
          inputStream.copyTo(response.outputStream)
        }
        response.outputStream.flush()

        log.debug("Served favicon.ico from static resources")
      } else {
        response.status = HttpServletResponse.SC_NOT_FOUND
        log.debug("favicon.ico not found, returning 404")
      }
    } catch (e: IOException) {
      log.error("Error serving favicon.ico", e)
      response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }
  }
}

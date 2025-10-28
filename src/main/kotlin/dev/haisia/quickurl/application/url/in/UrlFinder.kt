package dev.haisia.quickurl.application.url.`in`

import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UrlFinder {
  fun findOriginalUrl(shortKey: String): String
  fun findMyUrls(pageable: Pageable): Page<UrlWithClickCountDto>
}
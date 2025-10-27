package dev.haisia.quickurl.application.out

import dev.haisia.quickurl.domain.Url
import org.springframework.data.jpa.repository.JpaRepository

interface UrlRepository: JpaRepository<Url, Long> {
  fun findByOriginalUrl(originalUrl: String): Url?
  fun findByShortKey(shortKey: String): Url?
}
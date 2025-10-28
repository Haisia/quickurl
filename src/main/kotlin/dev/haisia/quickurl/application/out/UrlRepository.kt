package dev.haisia.quickurl.application.out

import dev.haisia.quickurl.domain.url.Url
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface UrlRepository: JpaRepository<Url, Long> {
  fun findByOriginalUrl(originalUrl: String): Url?
  fun findByShortKey(shortKey: String): Url?
  fun deleteByLastUsedAtBefore(threshold: LocalDateTime): Int
}

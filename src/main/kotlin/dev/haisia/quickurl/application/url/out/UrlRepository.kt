package dev.haisia.quickurl.application.url.out

import dev.haisia.quickurl.domain.url.Url
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface UrlRepository: JpaRepository<Url, Long> {
  fun findByOriginalUrlAndCreatedBy(originalUrl: String, createdBy: String): Url?
  fun findByShortKey(shortKey: String): Url?
  fun deleteByLastUsedAtBefore(threshold: LocalDateTime): Int
}

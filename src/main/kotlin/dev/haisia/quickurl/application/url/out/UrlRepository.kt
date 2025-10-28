package dev.haisia.quickurl.application.url.out

import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto
import dev.haisia.quickurl.domain.url.Url
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface UrlRepository: JpaRepository<Url, Long> {
  fun findByOriginalUrlAndCreatedBy(originalUrl: String, createdBy: String): Url?
  fun findByShortKey(shortKey: String): Url?
  fun deleteByLastUsedAtBefore(threshold: LocalDateTime): Int

  @Query(
    """
      SELECT 
        u.id as id,
        u.shortKey as shortKey,
        u.originalUrl as originalUrl,
        count(u.shortKey) as clickCount,
        u.createdBy as createdBy,
        u.lastUsedAt as lastUsedAt,
        u.createdAt as createdAt
      from Url u
      join ClickLog cl on cl.shortKey = u.shortKey
      where u.createdBy = :createdBy
      group by u.shortKey
    """
  )
  fun findByCreatedByWithClickCount(createdBy: String, pageable: Pageable): Page<UrlWithClickCountDto>

  @Modifying
  @Query(
    """
      delete from Url u
      where u.shortKey = :shortKey 
        and u.createdBy = :createdBy
    """
  )
  fun deleteByShortKeyAndCreatedBy(shortKey: String, createdBy: String): Int
}

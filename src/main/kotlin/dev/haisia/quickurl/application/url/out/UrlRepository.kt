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
    select new dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto(
      u.id, 
      u.shortKey,
      u.originalUrl, 
      COALESCE(COUNT(cl.id), 0L), 
      u.createdBy,
      u.lastUsedAt, 
      u.createdAt
    )
    from Url u 
    left join ClickLog cl ON cl.shortKey = u.shortKey
    where u.createdBy = :createdBy
    group by u.id, u.shortKey, u.originalUrl, u.createdBy, u.lastUsedAt, u.createdAt
    order by u.createdAt DESC
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

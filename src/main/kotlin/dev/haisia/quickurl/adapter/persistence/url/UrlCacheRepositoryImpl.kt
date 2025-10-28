package dev.haisia.quickurl.adapter.persistence.url

import dev.haisia.quickurl.application.out.UrlCacheRepository
import org.springframework.stereotype.Repository

@Repository
class UrlCacheRepositoryImpl(
  private val urlCacheRedisRepository: UrlCacheRedisRepository
) : UrlCacheRepository {

  override fun get(shortKey: String): String? {
    return urlCacheRedisRepository.findById(shortKey)
      .map { it.originalUrl }
      .orElse(null)
  }

  override fun set(shortKey: String, originalUrl: String) {
    val urlCache = UrlCache(shortKey = shortKey, originalUrl = originalUrl)
    urlCacheRedisRepository.save(urlCache)
  }

  override fun delete(shortKey: String) {
    urlCacheRedisRepository.deleteById(shortKey)
  }
}

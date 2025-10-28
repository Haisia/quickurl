package dev.haisia.quickurl.adapter.cache

import dev.haisia.quickurl.application.url.out.UrlCacheRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class UrlCacheRepositoryImpl(
  private val redisTemplate: RedisTemplate<String, String>
) : UrlCacheRepository {

  companion object {
    private const val KEY_PREFIX = "url_cache:"
    private const val TTL_HOURS = 24L
  }

  override fun get(shortKey: String): String? {
    return redisTemplate.opsForValue().get(KEY_PREFIX + shortKey)
  }

  override fun set(shortKey: String, originalUrl: String) {
    redisTemplate.opsForValue().set(
      KEY_PREFIX + shortKey,
      originalUrl,
      TTL_HOURS,
      TimeUnit.HOURS
    )
  }

  override fun delete(shortKey: String) {
    redisTemplate.delete(KEY_PREFIX + shortKey)
  }
}

package dev.haisia.quickurl.adapter.persistence.url

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.util.concurrent.TimeUnit

@RedisHash("url_cache")
data class UrlCache(
  @Id
  val shortKey: String,
  val originalUrl: String,
  @TimeToLive(unit = TimeUnit.HOURS)
  val ttl: Long = 24
)

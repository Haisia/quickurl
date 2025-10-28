package dev.haisia.quickurl.application.url

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UrlEventListener(
  private val redisTemplate: RedisTemplate<String, String>
) {
  companion object {
    private const val URL_CACHE_KEY_PREFIX = "url_cache:"
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleUrlDeleted(event: UrlEvent.UrlDeleted) {
    redisTemplate.delete("$URL_CACHE_KEY_PREFIX${event.shortKey}")
  }
}
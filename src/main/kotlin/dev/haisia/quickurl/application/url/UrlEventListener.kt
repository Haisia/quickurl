package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.shared.out.EmailSender
import dev.haisia.quickurl.application.url.out.UrlClickLogRepository
import dev.haisia.quickurl.application.url.out.UrlClickStatisticsRepository
import dev.haisia.quickurl.application.url.out.UrlRepository
import dev.haisia.quickurl.application.user.out.UserRepository
import dev.haisia.quickurl.domain.url.UrlClickLog
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Component
class UrlEventListener(
  private val redisTemplate: RedisTemplate<String, String>,
  private val urlRepository: UrlRepository,
  private val urlClickLogRepository: UrlClickLogRepository,
  private val urlClickStatisticsRepository: UrlClickStatisticsRepository,
  private val userRepository: UserRepository,
  private val mailSender: EmailSender
) {
  companion object {
    private const val URL_CACHE_KEY_PREFIX = "url_cache:"
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleUrlCreated(event: UrlEvent.UrlCreated) {
    val url = urlRepository.findById(event.urlId).getOrNull() ?: return

    if(url.createdBy != "anonymous") {
      val user = userRepository.findById(UUID.fromString(url.createdBy)).getOrNull() ?: return

      mailSender.sendUrlCreated(
        recipientEmail = user.email.value,
        recipientName = user.email.value,
        shortKey = url.getShortKeyOrThrow(),
        originalUrl = url.originalUrl,
      ).subscribe()
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleUrlDeleted(event: UrlEvent.UrlDeleted) {
    redisTemplate.delete("$URL_CACHE_KEY_PREFIX${event.shortKey}")
  }

  @Async
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  fun handleUrlClicked(event: UrlEvent.UrlClicked) {
    val dto = event.urlClickDto

    urlRepository.updateLastUsedAt(dto.shortKey)

    urlClickLogRepository.save(
      UrlClickLog.of(
        shortKey = dto.shortKey,
        ipAddress = dto.ipAddress,
        userAgent = dto.userAgent,
        referer = dto.referer
      )
    )

    urlClickStatisticsRepository.click()
  }
}
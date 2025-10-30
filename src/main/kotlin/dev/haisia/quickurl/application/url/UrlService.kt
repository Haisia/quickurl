package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.shared.out.AuthenticationContext
import dev.haisia.quickurl.application.url.dto.UrlClickDto
import dev.haisia.quickurl.application.url.dto.UrlWithClickCountDto
import dev.haisia.quickurl.application.url.`in`.UrlCleaner
import dev.haisia.quickurl.application.url.`in`.UrlClicker
import dev.haisia.quickurl.application.url.`in`.UrlCreator
import dev.haisia.quickurl.application.url.`in`.UrlFinder
import dev.haisia.quickurl.application.url.out.UrlCacheRepository
import dev.haisia.quickurl.application.url.out.UrlRepository
import dev.haisia.quickurl.application.user.UserNotFoundException
import dev.haisia.quickurl.application.user.out.UserRepository
import dev.haisia.quickurl.domain.Duration
import dev.haisia.quickurl.domain.url.OriginalUrl
import dev.haisia.quickurl.domain.url.Url
import dev.haisia.quickurl.domain.url.UrlEncoder
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class UrlService(
  private val urlRepository: UrlRepository,
  private val urlEncoder: UrlEncoder,
  private val urlCacheRepository: UrlCacheRepository,
  private val authenticationContext: AuthenticationContext,
  private val userRepository: UserRepository,
  private val eventPublisher: ApplicationEventPublisher,
) : UrlCreator, UrlFinder, UrlCleaner, UrlClicker {
  companion object {
    private val log = LoggerFactory.getLogger(UrlService::class.java)
  }

  override fun createShortKey(originalUrl: OriginalUrl, expirationDuration: Duration): String {
    val shortKey = createOrGetShortKey(originalUrl, expirationDuration)
    urlCacheRepository.set(shortKey, originalUrl)
    return shortKey
  }

  private fun createOrGetShortKey(originalUrl: OriginalUrl, expirationDuration: Duration): String {
    val userIdOrNull = authenticationContext.getCurrentUserIdAllowNull()

    val url = urlRepository.findByOriginalUrlAndCreatedBy(originalUrl, userIdOrNull?.toString() ?: "anonymous")
      ?: run {
        val url = when {
          userIdOrNull == null -> {
            Url.of(
              originalUrl = originalUrl,
              createdBy = userIdOrNull
            )
          }
          else -> {
            Url.of(
              originalUrl = originalUrl,
              expirationDuration = expirationDuration,
              createdBy = userIdOrNull
            )
          }
        }
        val saved = urlRepository.save(url)
        eventPublisher.publishEvent(UrlEvent.UrlCreated(saved.getIdOrThrow()))
        saved
      }

    if (!url.hasShortKey()) {
      url.generateShortKey(urlEncoder)
    }

    return url.getShortKeyOrThrow()
  }

  override fun findMyUrls(pageable: Pageable): Page<UrlWithClickCountDto> {
    val userId = authenticationContext.getCurrentUserId()
    val foundUser = userRepository.findById(userId).orElseThrow { UserNotFoundException() }

    return urlRepository.findByCreatedByWithClickCount(foundUser.getIdOrThrow().toString(), pageable)
  }

  override fun deleteUnusedUrls(thresholdMonths: Long): Int {
    val threshold = LocalDateTime.now().minusMonths(thresholdMonths)
    log.info("Deleting URLs not used since: {}", threshold)
    
    val deletedCount = urlRepository.deleteByLastUsedAtBeforeAndExpiresAtIsNull(threshold)
    
    log.info("Deleted {} unused URLs (threshold: {} months)", deletedCount, thresholdMonths)
    return deletedCount
  }

  override fun deleteExpiredUrls(): Int {
    log.info("Deleting expired URLs")
    val threshold = LocalDateTime.now().toLocalDate().atStartOfDay()
    val deletedCount = urlRepository.deleteByExpiresAtBefore(threshold)

    log.info("Deleted {} unused URLs (threshold: {} day)", deletedCount, threshold)
    return deletedCount
  }

  override fun deleteMyUrl(shortKey: String) {
    val userId = authenticationContext.getCurrentUserId()
    val userIdString = userId.toString()

    val deletedCount = urlRepository.deleteByShortKeyAndCreatedBy(shortKey, userIdString)

    if (deletedCount < 1) {
      throw ShortUrlNotFoundException("URL not found for key: $shortKey")
    }

    log.info("Deleted URL for key: {}", shortKey)
    eventPublisher.publishEvent(UrlEvent.UrlDeleted(shortKey))
  }

  override fun click(urlClickDto : UrlClickDto): OriginalUrl {
    val cachedUrl = urlCacheRepository.get(urlClickDto.shortKey)
    if (cachedUrl != null) {
      eventPublisher.publishEvent(UrlEvent.UrlClicked(urlClickDto))
      return OriginalUrl(cachedUrl)
    }

    val url = urlRepository.findByShortKey(urlClickDto.shortKey)
      ?: throw ShortUrlNotFoundException("URL not found for key: ${urlClickDto.shortKey}")

    urlCacheRepository.set(urlClickDto.shortKey, url.originalUrl)

    eventPublisher.publishEvent(UrlEvent.UrlClicked(urlClickDto))

    return url.originalUrl
  }
}
package dev.haisia.quickurl.application

import dev.haisia.quickurl.application.`in`.UrlCleaner
import dev.haisia.quickurl.application.`in`.UrlCreator
import dev.haisia.quickurl.application.`in`.UrlFinder
import dev.haisia.quickurl.application.out.UrlCacheRepository
import dev.haisia.quickurl.application.out.UrlRepository
import dev.haisia.quickurl.domain.Url
import dev.haisia.quickurl.domain.UrlEncoder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class UrlService(
  private val urlRepository: UrlRepository,
  private val urlEncoder: UrlEncoder,
  private val urlCacheRepository: UrlCacheRepository,
) : UrlCreator, UrlFinder, UrlCleaner {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createShortKey(originalUrl: String): String {
    val shortKey = createOrGetShortKey(originalUrl)
    urlCacheRepository.set(shortKey, originalUrl)
    return shortKey
  }

  private fun createOrGetShortKey(originalUrl: String): String {
    val url = urlRepository.findByOriginalUrl(originalUrl)
      ?: urlRepository.save(Url.of(originalUrl))

    if (!url.hasShortKey()) {
      url.generateShortKey(urlEncoder)
    }

    return url.requireShortKey()
  }

  override fun findOriginalUrl(shortKey: String): String {
    val cachedUrl = urlCacheRepository.get(shortKey)
    if (cachedUrl != null) {
      return cachedUrl
    }

    val url = urlRepository.findByShortKey(shortKey) ?: throw ShortUrlNotFoundException("URL not found for key: $shortKey")
    url.click()

    urlCacheRepository.set(shortKey, url.originalUrl)

    return url.originalUrl
  }

  override fun deleteUnusedUrls(thresholdMonths: Long): Int {
    val threshold = LocalDateTime.now().minusMonths(thresholdMonths)
    logger.info("Deleting URLs not used since: {}", threshold)
    
    val deletedCount = urlRepository.deleteByLastUsedAtBefore(threshold)
    
    logger.info("Deleted {} unused URLs (threshold: {} months)", deletedCount, thresholdMonths)
    return deletedCount
  }
}
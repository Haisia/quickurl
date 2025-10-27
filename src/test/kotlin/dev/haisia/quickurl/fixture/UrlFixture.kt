package dev.haisia.quickurl.fixture

import dev.haisia.quickurl.domain.Url
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

class UrlFixture {
  companion object {
    fun createUrl(
      id: Long? = null,
      shortKey: String = "abc123",
      originalUrl: String = "https://example.com",
      lastUsedAt: LocalDateTime? = null,
      createdAt: LocalDateTime? = null,
      isDeleted: Boolean? = null,
    ): Url {
      val url = Url.of(shortKey = shortKey, originalUrl = originalUrl)
      
      id?.let { url.setFieldValue("id", it) }
      createdAt?.let { url.setFieldValue("createdAt", it) }
      isDeleted?.let { url.setFieldValue("isDeleted", it) }
      lastUsedAt?.let { url.setFieldValue("lastUsedAt", it) }
      
      return url
    }
    
    private fun Url.setFieldValue(fieldName: String, value: Any?) {
      this::class.memberProperties
        .first { it.name == fieldName }
        .apply {
          isAccessible = true

          javaField?.apply {
            isAccessible = true
            set(this@setFieldValue, value)
          }
        }
    }
  }
}

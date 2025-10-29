package dev.haisia.quickurl.adapter.persistence.converter

import dev.haisia.quickurl.domain.url.OriginalUrl
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class OriginalUrlConverter : AttributeConverter<OriginalUrl, String> {

  override fun convertToDatabaseColumn(originalUrl: OriginalUrl?): String? {
    return originalUrl?.value
  }

  override fun convertToEntityAttribute(value: String?): OriginalUrl? {
    return value?.let { OriginalUrl(it) }
  }
}
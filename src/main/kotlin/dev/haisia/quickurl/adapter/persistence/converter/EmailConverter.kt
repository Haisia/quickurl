package dev.haisia.quickurl.adapter.persistence.converter

import dev.haisia.quickurl.domain.Email
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.let

@Converter(autoApply = true)
class EmailConverter : AttributeConverter<Email, String> {

  override fun convertToDatabaseColumn(email: Email?): String? {
    return email?.value
  }

  override fun convertToEntityAttribute(value: String?): Email? {
    return value?.let { Email(it) }
  }
}
package dev.haisia.quickurl.adapter.serializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import dev.haisia.quickurl.domain.Email
import dev.haisia.quickurl.domain.Password
import dev.haisia.quickurl.domain.url.OriginalUrl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
* Value Object를 위한 Jackson Module 설정
*
* 두 가지 형식을 모두 지원:
* 1. 간단한 형식: "email": "test@test.com"
* 2. 객체 형식: "email": {"value": "test@test.com"}
*/
@Configuration
class ValueObjectJacksonConfig {

  @Bean
  fun valueObjectModule(): SimpleModule {
    return SimpleModule().apply {
      addDeserializer(Email::class.java, EmailDeserializer())
      addDeserializer(Password::class.java, PasswordDeserializer())
      addDeserializer(OriginalUrl::class.java, OriginalUrlDeserializer())
    }
  }
}

/**
* Email Deserializer
* - String: "test@test.com" → Email("test@test.com")
* - Object: {"value": "test@test.com"} → Email("test@test.com")
*/
class EmailDeserializer : JsonDeserializer<Email>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Email {
    val node: JsonNode = p.codec.readTree(p)

    val emailValue = when {
      node.isTextual -> node.asText()
      node.isObject && node.has("value") -> node.get("value").asText()
      else -> throw IllegalArgumentException("Invalid email format. Expected string or object with 'value' field")
    }

    return Email(emailValue)
  }
}

/**
* Password Deserializer
* - String: "password123" → Password("password123")
* - Object: {"value": "password123"} → Password("password123")
*/
class PasswordDeserializer : JsonDeserializer<Password>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Password {
    val node: JsonNode = p.codec.readTree(p)

    val passwordValue = when {
      node.isTextual -> node.asText()
      node.isObject && node.has("value") -> node.get("value").asText()
      else -> throw IllegalArgumentException("Invalid password format. Expected string or object with 'value' field")
    }

    return Password(passwordValue)
  }
}

/**
* OriginalUrl Deserializer
* - String: "http://google.com" → OriginalUrl("http://google.com")
* - Object: {"value": "http://google.com"} → OriginalUrl("http://google.com")
*/
class OriginalUrlDeserializer : JsonDeserializer<OriginalUrl>() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OriginalUrl {
    val node: JsonNode = p.codec.readTree(p)

    val originalUrlValue = when {
      node.isTextual -> node.asText()
      node.isObject && node.has("value") -> node.get("value").asText()
      else -> throw IllegalArgumentException("Invalid url format. Expected string or object with 'value' field")
    }

    return OriginalUrl(originalUrlValue)
  }
}
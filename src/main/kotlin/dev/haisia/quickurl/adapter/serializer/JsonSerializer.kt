package dev.haisia.quickurl.adapter.serializer

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class JsonSerializer(
  private val objectMapper: ObjectMapper
) {
  /**
  * 어떤 객체든 JSON 문자열로 변환합니다.
  *
  * @param data JSON으로 변환할 데이터 객체 (DTO, 엔티티 등)
  * @return 직렬화된 JSON 문자열
  */
  fun <T> toJsonString(data: T): String {
    return try {
      objectMapper.writeValueAsString(data)
    } catch (e: Exception) {
      "{\"error\": \"Serialization failed for object: ${(data as Any).javaClass.simpleName}\"}"
    }
  }
}
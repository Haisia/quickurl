package dev.haisia.quickurl.adapter.web.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Page

data class ApiPageableData<T>(
  @JsonProperty("total_pages")
  val totalPages: Int,

  @JsonProperty("total_count")
  val totalCount: Long,

  @JsonProperty("items")
  val items: List<T>
) {
  companion object {
    fun <T> from(page: Page<T>): ApiPageableData<T> {
      return ApiPageableData(
        totalPages = page.totalPages,
        totalCount = page.totalElements,
        items = page.content)
    }
  }
}
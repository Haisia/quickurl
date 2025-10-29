package dev.haisia.quickurl.domain.url

interface UrlEncoder {
  fun encode(id: Long): String
}
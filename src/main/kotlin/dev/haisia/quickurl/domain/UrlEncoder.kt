package dev.haisia.quickurl.domain

interface UrlEncoder {
  fun encode(id: Long): String
}
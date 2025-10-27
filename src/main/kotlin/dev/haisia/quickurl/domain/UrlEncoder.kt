package dev.haisia.quickurl.domain

interface UrlEncoder {
  fun encode(id: Long): String
  fun decode(url: String): Long
}
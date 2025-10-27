package dev.haisia.quickurl.adapter.encoder

import dev.haisia.quickurl.domain.UrlEncoder
import org.springframework.stereotype.Component

@Component
class Base62Encoder: UrlEncoder {
  private val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  private val base = chars.length

  override fun encode(id: Long): String {
    if (id == 0L) return chars[0].toString()

    var num = id
    val sb = StringBuilder()

    while (num > 0) {
      sb.append(chars[(num % base).toInt()])
      num /= base
    }

    return sb.reverse().toString()
  }

  override fun decode(url: String): Long {
    var num = 0L
    for (char in url) {
      val index = chars.indexOf(char)
      if (index == -1) {
        throw IllegalArgumentException("Invalid character in encoded string: $char")
      }
      num = num * base + index
    }
    return num
  }
}

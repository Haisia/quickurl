package dev.haisia.quickurl.adapter.encoder

import dev.haisia.quickurl.domain.UrlEncoder
import org.springframework.stereotype.Component

@Component
class Base62Encoder: UrlEncoder {
  private val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

  override fun encode(id: Long): String {
    if (id == 0L) return "0"

    var num = id
    val sb = StringBuilder()

    while (num > 0) {
      sb.append(chars[(num % 62).toInt()])
      num /= 62
    }

    return sb.reverse().toString()
  }

  override fun decode(url: String): Long {
    var num = 0L
    for (char in url) {
      num = num * 62 + chars.indexOf(char)
    }
    return num
  }
}
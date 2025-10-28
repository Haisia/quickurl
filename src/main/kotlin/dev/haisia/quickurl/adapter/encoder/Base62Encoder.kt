package dev.haisia.quickurl.adapter.encoder

import dev.haisia.quickurl.domain.url.UrlEncoder
import org.springframework.stereotype.Component
import java.util.UUID

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

    // 난수 추가
    UUID.randomUUID().toString().substring(0, 8).let {sb.append(it)}

    return sb.reverse().toString()
  }
}

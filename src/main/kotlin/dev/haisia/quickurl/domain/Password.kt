package dev.haisia.quickurl.domain

import kotlin.text.matches
import kotlin.text.toRegex

data class Password(val value: String) {

  companion object {
    private val PASSWORD_REGEX = "^\\S{6,}$".toRegex()
  }

  init {
    require(value.matches(PASSWORD_REGEX)) {
      "Password must be at least 6 characters long (no spaces allowed)"
    }
  }
}

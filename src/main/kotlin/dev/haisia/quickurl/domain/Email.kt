package dev.haisia.quickurl.domain

data class Email(val value: String) {

  companion object {
    private val EMAIL_REGEX =
      "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+([.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}$".toRegex()
  }
  init {
    require(value.matches(EMAIL_REGEX)) {
      "Invalid email format: $value"
    }
  }
}

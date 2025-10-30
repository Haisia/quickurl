package dev.haisia.quickurl.domain

enum class Duration {
  ONE_DAY("1d", 1),
  SEVEN_DAYS("7d", 7),
  THIRTY_DAYS("30d", 30),
  NINETY_DAYS("90d", 90),
  NONE("none", null),
  ;

  val value: String
  val days: Int?

  constructor(value: String, days: Int?) {
    this.value = value
    this.days = days
  }
}
package dev.haisia.quickurl.domain

import java.lang.RuntimeException

abstract class DomainException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}
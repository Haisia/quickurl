package dev.haisia.quickurl.domain

abstract class DomainException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}

class IdNotGeneratedException(
  message: String = "Id not generated."
) : DomainException(message)
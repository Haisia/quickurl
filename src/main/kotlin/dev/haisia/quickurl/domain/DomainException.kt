package dev.haisia.quickurl.domain

import java.lang.RuntimeException

abstract class DomainException : RuntimeException {
  constructor(message: String) : super(message)
  constructor(message: String, cause: Throwable) : super(message, cause)
}

class ShortKeyGenerationException(
  message: String = "Id must not be null. Please save url before generating short key."
) : DomainException(message)

class ShortKeyNotGeneratedException(
  message: String = "Short key has not been generated yet. Call generateShortKey() first."
) : DomainException(message)
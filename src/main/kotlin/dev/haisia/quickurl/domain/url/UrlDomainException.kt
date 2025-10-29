package dev.haisia.quickurl.domain.url

import dev.haisia.quickurl.domain.DomainException

class ShortKeyGenerationException(
  message: String = "Id must not be null. Please save url before generating short key."
) : DomainException(message)

class ShortKeyNotGeneratedException(
  message: String = "Short key has not been generated yet. Call generateShortKey() first."
) : DomainException(message)

class InvalidOriginalUrlException(
  message: String
) : DomainException(message)

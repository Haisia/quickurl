package dev.haisia.quickurl.application.url

import dev.haisia.quickurl.application.shared.ApplicationException
import org.springframework.http.HttpStatus

class ShortUrlNotFoundException(
  message: String = "The requested short URL was not found.",
) : ApplicationException(message, HttpStatus.NOT_FOUND)
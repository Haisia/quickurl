package dev.haisia.quickurl.adapter.encoder

import dev.haisia.quickurl.adapter.AdapterException
import org.springframework.http.HttpStatus

abstract class EncoderException : AdapterException {
  constructor(message: String, httpStatus: HttpStatus) : super(message, httpStatus)
  constructor(message: String, httpStatus: HttpStatus, cause: Throwable) : super(message, httpStatus, cause)
}

class InvalidShortKeyFormatException(
  message: String = "Short URL key contains invalid characters."
) : EncoderException(message, HttpStatus.BAD_REQUEST)
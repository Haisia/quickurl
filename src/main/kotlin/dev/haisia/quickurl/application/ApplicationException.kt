package dev.haisia.quickurl.application

import org.springframework.http.HttpStatus

abstract class ApplicationException : RuntimeException {
  val httpStatus: HttpStatus

  constructor(message: String, httpStatus: HttpStatus) : super(message) {
    this.httpStatus = httpStatus
  }

  constructor(message: String, httpStatus: HttpStatus, cause: Throwable) : super(message, cause) {
    this.httpStatus = httpStatus
  }
}

class ShortUrlNotFoundException(
  message: String = "The requested short URL was not found.",
) : ApplicationException(message, HttpStatus.NOT_FOUND)
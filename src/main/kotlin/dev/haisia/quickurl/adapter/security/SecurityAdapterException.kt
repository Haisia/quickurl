package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.adapter.AdapterException
import org.springframework.http.HttpStatus

abstract class SecurityAdapterException : AdapterException {
  constructor(message: String, httpStatus: HttpStatus) : super(message, httpStatus)
  constructor(message: String, httpStatus: HttpStatus, cause: Throwable) : super(message, httpStatus, cause)
}

class UnauthorizedAdapterException(
  message: String = "Access is denied."
) : SecurityAdapterException(message, HttpStatus.FORBIDDEN)
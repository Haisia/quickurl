package dev.haisia.quickurl.adapter.persistence

import dev.haisia.quickurl.adapter.AdapterException
import org.springframework.http.HttpStatus

abstract class PersistenceAdapterException : AdapterException {
  constructor(message: String, httpStatus: HttpStatus) : super(message, httpStatus)
  constructor(message: String, httpStatus: HttpStatus, cause: Throwable) : super(message, httpStatus, cause)
}

class EntityNotFoundException(
  message: String = "Entity not found."
) : PersistenceAdapterException(message, HttpStatus.NOT_FOUND)

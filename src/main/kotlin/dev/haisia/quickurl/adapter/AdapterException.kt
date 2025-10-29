package dev.haisia.quickurl.adapter

import org.springframework.http.HttpStatus

abstract class AdapterException : RuntimeException {
  val httpStatus: HttpStatus

  constructor(message: String, httpStatus: HttpStatus) : super(message) {
    this.httpStatus = httpStatus
  }

  constructor(message: String, httpStatus: HttpStatus, cause: Throwable) : super(message, cause) {
    this.httpStatus = httpStatus
  }
}
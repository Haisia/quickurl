package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.application.shared.ApplicationException
import org.springframework.http.HttpStatus

class LoginFailedException(
  message: String = "Login failed. Please check your email and password.",
) : ApplicationException(message, HttpStatus.NOT_FOUND)

class EmailAlreadyRegisteredException(
  message: String = "Email is already registered."
) : ApplicationException(message, HttpStatus.BAD_REQUEST)

class InvalidTokenAdapterException(
  message: String = "Invalid authentication token."
) : ApplicationException(message, HttpStatus.UNAUTHORIZED)

class TokenExpiredAdapterException(
  message: String = "Authentication token has expired."
) : ApplicationException(message, HttpStatus.UNAUTHORIZED)

class UnauthorizedAdapterException(
  message: String = "Access is denied."
) : ApplicationException(message, HttpStatus.FORBIDDEN)

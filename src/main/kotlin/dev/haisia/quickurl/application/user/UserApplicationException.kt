package dev.haisia.quickurl.application.user

import dev.haisia.quickurl.application.shared.ApplicationException
import org.springframework.http.HttpStatus

class LoginFailedException(
  message: String = "Login failed. Please check your email and password.",
) : ApplicationException(message, HttpStatus.NOT_FOUND)

class EmailAlreadyRegisteredException(
  message: String = "Email is already registered."
) : ApplicationException(message, HttpStatus.BAD_REQUEST)

class UnauthorizedException(
  message: String = "Access is denied."
) : ApplicationException(message, HttpStatus.FORBIDDEN)

class UserNotFoundException(
  message: String = "User not found."
) : ApplicationException(message, HttpStatus.NOT_FOUND)
package dev.haisia.quickurl.domain.user

import dev.haisia.quickurl.domain.DomainException

class UserIdMissingException(
  message: String = "User ID is required but was found to be null. Check the entity persistence status."
) : DomainException(message)
package dev.haisia.quickurl.domain

interface PasswordEncoder {
  fun encode(password: String) :String
  fun matches(password: String, hashedPassword: String) : Boolean
}
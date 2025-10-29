package dev.haisia.quickurl.adapter.email

data class BrevoEmailRequest(
  val sender: EmailContact,
  val to: List<EmailContact>,
  val subject: String,
  val htmlContent: String,
  val textContent: String? = null
)

data class EmailContact(
  val email: String,
  val name: String
)

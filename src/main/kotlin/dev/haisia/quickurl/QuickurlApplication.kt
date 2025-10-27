package dev.haisia.quickurl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuickurlApplication

fun main(args: Array<String>) {
  runApplication<QuickurlApplication>(*args)
}

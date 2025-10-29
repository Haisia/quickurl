package dev.haisia.quickurl.adapter.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AuthPageController {

  @GetMapping("/register")
  fun registerPage(): String {
    return "register"
  }

  @GetMapping("/login")
  fun loginPage(): String {
    return "login"
  }
}

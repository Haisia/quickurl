package dev.haisia.quickurl.adapter.web.page

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MainController {

  @GetMapping("/")
  fun index() = "index"

}
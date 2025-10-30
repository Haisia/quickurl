package dev.haisia.quickurl.adapter.web.page

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 부하 테스트용 더미 페이지 컨트롤러
 * k6 등의 부하 테스트 시 리다이렉트 대상으로 사용
 */
@Controller
@RequestMapping("/test")
class TestPageController {
  
  companion object {
    private val log = LoggerFactory.getLogger(TestPageController::class.java)
  }
  /**
   * 리다이렉트 테스트 대상 페이지
   * 
   * 사용 예:
   * 1. 단축 URL 생성: POST /api/v1/url/shorten { "originalUrl": "http://localhost:8080/test/redirect-target" }
   * 2. k6로 단축 URL 호출하여 이 페이지로 리다이렉트
   */
  @GetMapping("/redirect-target/{dummy}")
  fun redirectTarget(@PathVariable dummy: String): String {
    log.info("Requested dummy: $dummy")
    return "test-redirect-target"
  }
}

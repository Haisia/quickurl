package dev.haisia.quickurl.adapter.web.page

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

/**
* 사용자가 등록한 URL을 관리하는 페이지 컨트롤러
*/
@Controller
@RequestMapping("/urls")
class UrlManagePageController {

  /**
  * 자신이 등록한 URL 목록 조회 페이지
  *
  * @param pageable 페이지네이션 정보 (size, page, sort)
  * @param model 뷰에 전달할 데이터
  * @return URL 관리 페이지 템플릿 경로
  */
  @GetMapping("/my")
  fun myUrls(
    @PageableDefault(size = 20, sort = ["createdAt"], direction = DESC) pageable: Pageable,
    model: Model
  ): String {
    // 페이지 정보를 모델에 추가 (JavaScript에서 API 호출 시 사용)
    model.addAttribute("pageNumber", pageable.pageNumber)
    model.addAttribute("pageSize", pageable.pageSize)
    
    return "urls/my-urls"
  }
}

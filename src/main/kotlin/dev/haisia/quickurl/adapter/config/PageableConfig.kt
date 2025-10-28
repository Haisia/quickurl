package dev.haisia.quickurl.adapter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class PageableConfig : WebMvcConfigurer {
  
  @Bean
  fun customPageableResolver(): PageableHandlerMethodArgumentResolver {
    return CustomPageableHandlerMethodArgumentResolver()
  }

  override fun addArgumentResolvers(resolvers: MutableList<org.springframework.web.method.support.HandlerMethodArgumentResolver>) {
    resolvers.add(customPageableResolver())
  }
}

/**
 * Pageable 객체의 size, page 값에 대한 방어 코드를 포함한 커스텀 Resolver
 */
class CustomPageableHandlerMethodArgumentResolver : PageableHandlerMethodArgumentResolver() {

  companion object {
    private const val MAX_PAGE_SIZE = 100
    private const val DEFAULT_PAGE_SIZE = 20
    private const val MAX_PAGE_NUMBER = 1000
  }

  override fun resolveArgument(
    parameter: MethodParameter,
    mavContainer: ModelAndViewContainer?,
    webRequest: NativeWebRequest,
    binderFactory: WebDataBinderFactory?
  ): Pageable {
    val pageable = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory)
    
    // size 값 방어: 최대값 초과 시 제한
    val safeSize = when {
      pageable.pageSize > MAX_PAGE_SIZE -> MAX_PAGE_SIZE
      pageable.pageSize <= 0 -> DEFAULT_PAGE_SIZE
      else -> pageable.pageSize
    }
    
    // page 값 방어: 최대값 초과 시 제한
    val safePage = when {
      pageable.pageNumber > MAX_PAGE_NUMBER -> MAX_PAGE_NUMBER
      pageable.pageNumber < 0 -> 0
      else -> pageable.pageNumber
    }
    
    // 방어된 값으로 새로운 Pageable 생성
    return PageRequest.of(
      safePage,
      safeSize,
      pageable.sort
    )
  }
}

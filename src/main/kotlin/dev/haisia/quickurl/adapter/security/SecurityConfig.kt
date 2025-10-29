package dev.haisia.quickurl.adapter.security

import dev.haisia.quickurl.adapter.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .formLogin { it.disable() }
      .httpBasic { it.disable() }
      .sessionManagement {
        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      }
      .exceptionHandling { exceptions ->
        exceptions
          .authenticationEntryPoint { request, response, _ ->
            val requestURI = request.requestURI
            
            // API 요청인 경우 JSON 에러 응답
            if (requestURI.startsWith("/api/")) {
              response.sendError(401, "Unauthorized")
            }
            // 페이지 요청인 경우
            else if (requestURI.startsWith("/urls/")) {
              // URL 관리 페이지 접근 시 로그인 페이지로 리다이렉트
              response.sendRedirect("/login?returnUrl=${requestURI}")
            }
            else {
              // 그 외의 경우 에러 페이지로 포워드
              request.setAttribute("jakarta.servlet.error.status_code", 401)
              request.setAttribute("jakarta.servlet.error.message", "인증이 필요합니다.")
              request.setAttribute("jakarta.servlet.error.request_uri", requestURI)
              request.getRequestDispatcher("/error").forward(request, response)
            }
          }
          .accessDeniedHandler { request, response, _ ->
            val requestURI = request.requestURI
            
            // API 요청인 경우 JSON 에러 응답
            if (requestURI.startsWith("/api/")) {
              response.sendError(403, "Forbidden")
            }
            // 페이지 요청인 경우 에러 페이지로 포워드
            else {
              request.setAttribute("jakarta.servlet.error.status_code", 403)
              request.setAttribute("jakarta.servlet.error.message", "접근 권한이 없습니다.")
              request.setAttribute("jakarta.servlet.error.request_uri", requestURI)
              request.getRequestDispatcher("/error").forward(request, response)
            }
          }
      }

      .authorizeHttpRequests { auth ->
        auth
          // 정적 리소스 허용
          .requestMatchers(
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico"
          ).permitAll()
          // 공개 페이지 허용
          .requestMatchers(
            "/",
            "/register",
            "/login",
            "/error"  // 에러 페이지 허용
          ).permitAll()
          // 공개 API 허용
          .requestMatchers(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/me",
            "/api/v1/auth/token/refresh",
            "/api/v1/url/shorten/**",
            "/api/v1/stats/**",
            "/{shortUrl}",
            "/test/**"
          ).permitAll()
          .requestMatchers("/actuator/**").permitAll()
          .anyRequest().authenticated()
      }
      .addFilterBefore(
        jwtAuthenticationFilter,
        UsernamePasswordAuthenticationFilter::class.java
      )

    return http.build()
  }
}
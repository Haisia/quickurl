package dev.haisia.quickurl.adapter.config

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
            "/login"
          ).permitAll()
          // 공개 API 허용
          .requestMatchers(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/logout",
            "/api/v1/auth/me",
            "/api/v1/auth/token/refresh",
            "/api/v1/shorten",
            "/{shortUrl}"
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

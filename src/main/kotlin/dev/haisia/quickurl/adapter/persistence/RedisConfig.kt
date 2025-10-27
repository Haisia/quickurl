package dev.haisia.quickurl.adapter.persistence

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig {

  @Bean
  fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
    return RedisTemplate<String, Any>().apply {
      setConnectionFactory(connectionFactory)
      keySerializer = StringRedisSerializer()
      valueSerializer = StringRedisSerializer()
      hashKeySerializer = StringRedisSerializer()
      hashValueSerializer = StringRedisSerializer()
    }
  }
}
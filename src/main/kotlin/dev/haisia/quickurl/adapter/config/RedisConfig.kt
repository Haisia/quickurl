package dev.haisia.quickurl.adapter.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
@EnableRedisRepositories
class RedisConfig {

  /**
  * RedisTemplate 설정
  * 일반적인 Redis 작업 및 Rate Limiting에 사용
  */
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

  /**
   * Long 값 전용 RedisTemplate 설정
   * 클릭 통계 등 숫자 데이터 저장에 사용
   */
  @Bean
  fun longRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Long> {
    return RedisTemplate<String, Long>().apply {
      setConnectionFactory(connectionFactory)
      keySerializer = StringRedisSerializer()
      valueSerializer = StringRedisSerializer() // Long을 String으로 저장
      hashKeySerializer = StringRedisSerializer()
      hashValueSerializer = StringRedisSerializer()
    }
  }

  /**
  * Spring Cache Manager 설정
  * @Cacheable 등의 어노테이션에 사용
  */
  @Bean
  fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
    val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ofMinutes(10))
      .serializeKeysWith(
        RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
      )
      .serializeValuesWith(
        RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
      )
      .disableCachingNullValues()

    // 캐시별 TTL 설정
    val cacheConfigurations = mapOf(
      "urlCache" to defaultConfig.entryTtl(Duration.ofHours(1))
    )

    return RedisCacheManager.builder(connectionFactory)
      .cacheDefaults(defaultConfig)
      .withInitialCacheConfigurations(cacheConfigurations)
      .build()
  }
}

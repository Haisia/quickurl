package dev.haisia.quickurl.adapter.config

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun getAsyncExecutor(): Executor {
    val executor = ThreadPoolTaskExecutor()
    executor.corePoolSize = 5
    executor.maxPoolSize = 10
    executor.queueCapacity = 100
    executor.setThreadNamePrefix("async-click-log-")
    executor.setWaitForTasksToCompleteOnShutdown(true)
    executor.setAwaitTerminationSeconds(60)
    executor.initialize()
    return executor
  }

  override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
    return AsyncUncaughtExceptionHandler { ex, method, params ->
      logger.error(
        "Async method '{}' threw exception with params: {}",
        method.name,
        params.joinToString(", "),
        ex
      )
    }
  }
}

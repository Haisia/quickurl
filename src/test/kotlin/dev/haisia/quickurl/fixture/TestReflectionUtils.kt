package dev.haisia.quickurl.fixture

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

object TestReflectionUtils {
  
  /**
   * 객체의 필드 값을 리플렉션으로 강제 설정합니다.
   * 
   * @param fieldName 설정할 필드 이름
   * @param value 설정할 값
   * @throws NoSuchElementException 필드를 찾을 수 없는 경우
   */
  inline fun <reified T : Any> T.setFieldValue(fieldName: String, value: Any?) {
    T::class.memberProperties
      .firstOrNull { it.name == fieldName }
      ?.apply {
        isAccessible = true
        javaField?.apply {
          isAccessible = true
          set(this@setFieldValue, value)
        } ?: throw IllegalStateException("Field '$fieldName' has no backing field")
      }
      ?: throw NoSuchElementException("Field '$fieldName' not found in ${T::class.simpleName}")
  }
}

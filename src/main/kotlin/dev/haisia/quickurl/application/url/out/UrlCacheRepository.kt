package dev.haisia.quickurl.application.url.out

import dev.haisia.quickurl.domain.url.OriginalUrl

/**
 * URL 캐시 저장소 인터페이스
 * Redis를 통해 단축 URL과 원본 URL 매핑을 캐싱
 */
interface UrlCacheRepository {

  /**
   * 단축 키로 캐시된 원본 URL 조회
   *
   * @param shortKey 단축 URL의 키
   * @return 캐시된 원본 URL (없으면 null)
   */
  fun get(shortKey: String): String?

  /**
   * 단축 키와 원본 URL을 캐시에 저장
   *
   * @param shortKey 단축 URL의 키
   * @param originalUrl 원본 URL
   */
  fun set(shortKey: String, originalUrl: OriginalUrl)

  /**
   * 캐시에서 단축 키 삭제
   *
   * @param shortKey 삭제할 단축 URL의 키
   */
  fun delete(shortKey: String)
}

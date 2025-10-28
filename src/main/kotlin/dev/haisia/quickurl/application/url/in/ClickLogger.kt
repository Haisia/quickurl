package dev.haisia.quickurl.application.url.`in`

interface ClickLogger {
  /**
   * 클릭 이벤트를 비동기로 로깅합니다.
   * 
   * @param shortKey 단축 URL 키
   * @param ipAddress 클라이언트 IP 주소
   * @param userAgent User-Agent 헤더
   * @param referer Referer 헤더
   */
  fun logClickAsync(
    shortKey: String,
    ipAddress: String?,
    userAgent: String?,
    referer: String?
  )
}

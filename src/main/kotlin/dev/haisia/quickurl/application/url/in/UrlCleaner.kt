package dev.haisia.quickurl.application.url.`in`

interface UrlCleaner {
  /**
   * 지정된 개월 수 이상 사용하지 않은 URL을 삭제합니다.
   * @param thresholdMonths 삭제 기준 개월 수 (기본값: 3개월)
   * @return 삭제된 URL 개수
   */
  fun deleteUnusedUrls(thresholdMonths: Long = 3): Int

  fun deleteMyUrl(shortKey: String)
}

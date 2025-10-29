package dev.haisia.quickurl.application.url.`in`

interface UrlClickLogger {
  /* <today, total> */
  fun getGlobalClickStats(): Pair<Long, Long>
}

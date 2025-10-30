package dev.haisia.quickurl.domain

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@DisplayName("Duration 테스트")
class DurationTest {

  @Test
  @DisplayName("ONE_DAY는 1일의 기간을 나타낸다")
  fun oneDayHasOneDay() {
    assert(Duration.ONE_DAY.value == "1d")
    assert(Duration.ONE_DAY.days == 1)
  }

  @Test
  @DisplayName("SEVEN_DAYS는 7일의 기간을 나타낸다")
  fun sevenDaysHasSevenDays() {
    assert(Duration.SEVEN_DAYS.value == "7d")
    assert(Duration.SEVEN_DAYS.days == 7)
  }

  @Test
  @DisplayName("THIRTY_DAYS는 30일의 기간을 나타낸다")
  fun thirtyDaysHasThirtyDays() {
    assert(Duration.THIRTY_DAYS.value == "30d")
    assert(Duration.THIRTY_DAYS.days == 30)
  }

  @Test
  @DisplayName("NINETY_DAYS는 90일의 기간을 나타낸다")
  fun ninetyDaysHasNinetyDays() {
    assert(Duration.NINETY_DAYS.value == "90d")
    assert(Duration.NINETY_DAYS.days == 90)
  }

  @Test
  @DisplayName("NONE은 기간이 없음을 나타낸다")
  fun noneHasNoDuration() {
    assert(Duration.NONE.value == "none")
    assert(Duration.NONE.days == null)
  }

  @ParameterizedTest
  @EnumSource(Duration::class)
  @DisplayName("모든 Duration은 유효한 value를 가진다")
  fun allDurationsHaveValidValue(duration: Duration) {
    assert(duration.value.isNotBlank())
  }
}

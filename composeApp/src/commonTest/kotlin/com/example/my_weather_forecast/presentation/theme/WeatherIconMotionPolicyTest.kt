package com.example.my_weather_forecast.presentation.theme

import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherIconMotionPolicyTest {

    @Test
    fun givenEveryCondition_whenMapped_thenItUsesTheApprovedGesture() {
        val expected = mapOf(
            (WeatherIcon.CLEAR to true) to ConditionMotion.TURN,
            (WeatherIcon.CLEAR to false) to ConditionMotion.BREATHE,
            (WeatherIcon.CLOUDS to true) to ConditionMotion.DRIFT,
            (WeatherIcon.DRIZZLE to true) to ConditionMotion.DRIZZLE_FALL,
            (WeatherIcon.RAIN to true) to ConditionMotion.RAIN_FALL,
            (WeatherIcon.SNOW to true) to ConditionMotion.SNOW_FALL,
            (WeatherIcon.THUNDERSTORM to true) to ConditionMotion.THUNDER_PULSE,
            (WeatherIcon.ATMOSPHERE to true) to ConditionMotion.HAZE_DRIFT,
            (WeatherIcon.UNKNOWN to true) to ConditionMotion.NONE,
        )

        expected.forEach { (condition, gesture) ->
            val (icon, isDaytime) = condition
            assertEquals(
                gesture,
                WeatherIconMotionPolicy.conditionMotion(icon = icon, isDaytime = isDaytime),
                "$icon (daytime=$isDaytime)",
            )
        }
    }

    @Test
    fun givenDayAndNight_whenOnlyClearDiffers_thenOtherConditionsKeepOneGesture() {
        WeatherIcon.entries.filter { it != WeatherIcon.CLEAR }.forEach { icon ->
            assertEquals(
                WeatherIconMotionPolicy.conditionMotion(icon = icon, isDaytime = true),
                WeatherIconMotionPolicy.conditionMotion(icon = icon, isDaytime = false),
                "$icon must not change gesture between day and night",
            )
        }
    }

    @Test
    fun givenAStaticRequest_whenResolved_thenItNeverAnimates() {
        listOf(true, false).forEach { reducedMotion ->
            listOf(true, false).forEach { active ->
                assertEquals(
                    WeatherIconMotion.STATIC,
                    WeatherIconMotionPolicy.resolve(
                        requested = WeatherIconMotion.STATIC,
                        reducedMotion = reducedMotion,
                        active = active,
                    ),
                    "reducedMotion=$reducedMotion active=$active",
                )
            }
        }
    }

    @Test
    fun givenReducedMotion_whenHeroIsRequested_thenItFallsBackToStatic() {
        assertEquals(
            WeatherIconMotion.STATIC,
            WeatherIconMotionPolicy.resolve(
                requested = WeatherIconMotion.HERO,
                reducedMotion = true,
                active = true,
            ),
        )
    }

    @Test
    fun givenAnInactiveHero_whenResolved_thenItFallsBackToStatic() {
        assertEquals(
            WeatherIconMotion.STATIC,
            WeatherIconMotionPolicy.resolve(
                requested = WeatherIconMotion.HERO,
                reducedMotion = false,
                active = false,
            ),
        )
    }

    @Test
    fun givenEveryAnimatedGesture_whenTimed_thenItIncludesAQuietRestInterval() {
        ConditionMotion.entries.filter { it != ConditionMotion.NONE }.forEach { gesture ->
            val timing = WeatherIconMotionPolicy.timing(gesture)
            assertTrue(timing.cycleMillis > 0, "$gesture must move")
            assertTrue(timing.restMillis > 0, "$gesture must rest between cycles")
        }
    }

    @Test
    fun givenTheStaticGesture_whenTimed_thenItNeverMoves() {
        assertEquals(0, WeatherIconMotionPolicy.timing(ConditionMotion.NONE).cycleMillis)
    }

    @Test
    fun givenThunder_whenTimed_thenPulsesStayFurtherApartThanTheMinimumInterval() {
        val timing = WeatherIconMotionPolicy.timing(ConditionMotion.THUNDER_PULSE)

        assertTrue(
            timing.cycleMillis + timing.restMillis >= WeatherMotion.HeroThunderMinimumIntervalMillis,
            "Thunder repeated every ${timing.cycleMillis + timing.restMillis} ms, which is faster " +
                "than the ${WeatherMotion.HeroThunderMinimumIntervalMillis} ms minimum",
        )
    }

    @Test
    fun givenPrecipitation_whenTimed_thenDrizzleFallsSlowerThanRain() {
        val drizzle = WeatherIconMotionPolicy.timing(ConditionMotion.DRIZZLE_FALL)
        val rain = WeatherIconMotionPolicy.timing(ConditionMotion.RAIN_FALL)

        assertTrue(
            drizzle.cycleMillis > rain.cycleMillis,
            "Drizzle (${drizzle.cycleMillis} ms) must fall slower than rain (${rain.cycleMillis} ms)",
        )
    }

    @Test
    fun givenAnActiveHeroWithoutReducedMotion_whenResolved_thenItAnimates() {
        assertEquals(
            WeatherIconMotion.HERO,
            WeatherIconMotionPolicy.resolve(
                requested = WeatherIconMotion.HERO,
                reducedMotion = false,
                active = true,
            ),
        )
    }
}

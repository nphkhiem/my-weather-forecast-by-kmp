package com.example.my_weather_forecast.presentation.theme

import com.example.my_weather_forecast.domain.model.WeatherIcon

/**
 * How a caller wants a weather icon to behave. Compact instances in scrolling lists always ask for
 * [STATIC]; only the Detail hero asks for [HERO].
 */
enum class WeatherIconMotion {
    STATIC,
    HERO,
}

/** The single low-amplitude ambient gesture a condition uses while its icon runs in hero mode. */
enum class ConditionMotion {
    NONE,
    TURN,
    BREATHE,
    DRIFT,
    DRIZZLE_FALL,
    RAIN_FALL,
    SNOW_FALL,
    THUNDER_PULSE,
    HAZE_DRIFT,
}

/**
 * Timing for one ambient gesture: an active [cycleMillis] window followed by a quiet
 * [restMillis] pause, so hero motion settles instead of looping relentlessly.
 *
 * Precipitation repeats its fall [repeatsPerCycle] times inside a single active window, which
 * keeps the rest reading as a lull rather than a stutter between individual drops.
 */
data class ConditionMotionTiming(
    val cycleMillis: Int,
    val restMillis: Int,
    val repeatsPerCycle: Int = 1,
) {
    val totalMillis: Int get() = cycleMillis + restMillis
}

/**
 * Decides whether a weather icon animates at all, and which gesture it uses when it does. Kept
 * free of Compose so the approved motion matrix can be asserted directly.
 */
object WeatherIconMotionPolicy {

    /**
     * Hero motion survives only when the caller asked for it, the platform is not in a
     * reduced-motion state, and the hero is actually being looked at. Everything else is static.
     */
    fun resolve(
        requested: WeatherIconMotion,
        reducedMotion: Boolean,
        active: Boolean,
    ): WeatherIconMotion = when {
        requested == WeatherIconMotion.STATIC -> WeatherIconMotion.STATIC
        reducedMotion -> WeatherIconMotion.STATIC
        !active -> WeatherIconMotion.STATIC
        else -> WeatherIconMotion.HERO
    }

    /** Daytime only distinguishes [WeatherIcon.CLEAR]; every other condition reads the same way. */
    fun conditionMotion(icon: WeatherIcon, isDaytime: Boolean): ConditionMotion = when (icon) {
        WeatherIcon.CLEAR -> if (isDaytime) ConditionMotion.TURN else ConditionMotion.BREATHE
        WeatherIcon.CLOUDS -> ConditionMotion.DRIFT
        WeatherIcon.DRIZZLE -> ConditionMotion.DRIZZLE_FALL
        WeatherIcon.RAIN -> ConditionMotion.RAIN_FALL
        WeatherIcon.SNOW -> ConditionMotion.SNOW_FALL
        WeatherIcon.THUNDERSTORM -> ConditionMotion.THUNDER_PULSE
        WeatherIcon.ATMOSPHERE -> ConditionMotion.HAZE_DRIFT
        WeatherIcon.UNKNOWN -> ConditionMotion.NONE
    }

    /**
     * Thunder is deliberately the slowest gesture here: one restrained pulse, then a long quiet
     * gap, so the bolt never reads as a rapid flicker.
     */
    fun timing(motion: ConditionMotion): ConditionMotionTiming = when (motion) {
        ConditionMotion.NONE -> ConditionMotionTiming(cycleMillis = 0, restMillis = 0)

        ConditionMotion.TURN,
        ConditionMotion.BREATHE,
        ConditionMotion.DRIFT,
        -> ConditionMotionTiming(
            cycleMillis = WeatherMotion.HeroAmbientDurationMillis,
            restMillis = WeatherMotion.HeroAmbientRestMillis,
        )

        ConditionMotion.HAZE_DRIFT -> ConditionMotionTiming(
            cycleMillis = WeatherMotion.HeroAmbientDurationMillis * 2,
            restMillis = WeatherMotion.HeroAmbientRestMillis,
        )

        ConditionMotion.RAIN_FALL -> ConditionMotionTiming(
            cycleMillis = RAIN_DROP_MILLIS * PRECIPITATION_REPEATS,
            restMillis = WeatherMotion.HeroAmbientRestMillis,
            repeatsPerCycle = PRECIPITATION_REPEATS,
        )

        ConditionMotion.DRIZZLE_FALL -> ConditionMotionTiming(
            cycleMillis = DRIZZLE_DROP_MILLIS * PRECIPITATION_REPEATS,
            restMillis = WeatherMotion.HeroAmbientRestMillis,
            repeatsPerCycle = PRECIPITATION_REPEATS,
        )

        ConditionMotion.SNOW_FALL -> ConditionMotionTiming(
            cycleMillis = SNOW_FLAKE_MILLIS * PRECIPITATION_REPEATS,
            restMillis = WeatherMotion.HeroAmbientRestMillis,
            repeatsPerCycle = PRECIPITATION_REPEATS,
        )

        ConditionMotion.THUNDER_PULSE -> ConditionMotionTiming(
            cycleMillis = THUNDER_PULSE_MILLIS,
            restMillis = WeatherMotion.HeroThunderMinimumIntervalMillis - THUNDER_PULSE_MILLIS,
        )
    }

    private const val RAIN_DROP_MILLIS = 850
    private const val DRIZZLE_DROP_MILLIS = 1_300
    private const val SNOW_FLAKE_MILLIS = 2_000
    private const val PRECIPITATION_REPEATS = 3
    private const val THUNDER_PULSE_MILLIS = 1_200
}

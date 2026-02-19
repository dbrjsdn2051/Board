package com.example.hotarticle.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

class TimeCalculatorUtils {

    companion object {
        fun calculateDurationToMidnight(): Duration {
            val midnight = LocalDateTime.now().plusDays(1).with(LocalTime.MIDNIGHT)
            return Duration.between(LocalDateTime.now(), midnight)
        }
    }
}
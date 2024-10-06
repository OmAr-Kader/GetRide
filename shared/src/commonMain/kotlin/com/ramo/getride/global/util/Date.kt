@file:Suppress("unused")

package com.ramo.getride.global.util

inline val dateNow: String
    get() = kotlinx.datetime.Clock.System.now().toString()

inline val dateBeforeHourInstant: kotlinx.datetime.Instant
    get() = kotlinx.datetime.Clock.System.now().toEpochMilliseconds().let {
        kotlinx.datetime.Instant.fromEpochMilliseconds(it - 3600000)
    }

inline val dateBeforeHour: String
    get() = dateBeforeHourInstant.toString()

inline val dateNowMills: Long
    get() = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
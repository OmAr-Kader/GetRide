package com.ramo.getride.global.util

import kotlinx.datetime.Clock

inline val dateNow: String
    get() = Clock.System.now().toString()

inline val dateNowMills: Long
    get() = Clock.System.now().toEpochMilliseconds()

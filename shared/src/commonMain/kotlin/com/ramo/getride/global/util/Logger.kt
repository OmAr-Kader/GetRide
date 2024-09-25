package com.ramo.getride.global.util

fun logger(tag: String = "", error: String) {
    com.ramo.getride.di.getKoinInstance<org.lighthousegames.logging.KmLog>().w(tag = "==> $tag") { error }
}

fun loggerError(tag: String = "", error: String) {
    com.ramo.getride.di.getKoinInstance<org.lighthousegames.logging.KmLog>().w(tag = "==> $tag") { error }
}

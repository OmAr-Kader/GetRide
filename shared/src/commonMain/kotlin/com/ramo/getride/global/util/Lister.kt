@file:Suppress("unused")

package com.ramo.getride.global.util

fun <T> MutableList<T>.replace(predicate: (T) -> Boolean, invoke: (T) -> T): List<T> {
    val index = indexOfFirst(predicate)
    if (index == -1) {
        return this
    }
    this[index] = invoke(this[index])
    return this.toList()
}

fun <T> MutableList<T>.replace(predicate: (T) -> Boolean, invoke: (T) -> T, newInvoke: (List<T>, T?) -> Unit) {
    val index = indexOfFirst(predicate)
    if (index == -1) {
        newInvoke(this, null)
        return
    }
    invoke(this[index]).also {
        this[index] = it
    }.also {
        newInvoke(this.toList(), it)
    }
    return
}
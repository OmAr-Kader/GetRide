package com.ramo.getride.data.util

import com.ramo.getride.global.util.loggerError

suspend inline fun <reified T> io.github.jan.supabase.postgrest.result.PostgrestResult.toListOfObject(json: kotlinx.serialization.json.Json): List<T>? {
    return try {
        kotlinx.coroutines.coroutineScope {
            json.decodeFromString<List<T>?>(data)
        }
    } catch (e: kotlinx.serialization.SerializationException) {
        loggerError("toListOfObject", e)
        null
    } catch (e: IllegalArgumentException) {
        loggerError("toListOfObject", e)
        null
    }
}

suspend inline fun <reified T : Any> supabase(
    crossinline operation: suspend () -> T?,
): T? {
    return try {
        kotlinx.coroutines.coroutineScope {
            operation()
        }
    } catch (e: io.github.jan.supabase.exceptions.RestException) {
        loggerError(error = e)
        null
    } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
        loggerError(error = e)
        null
    } catch (e: io.github.jan.supabase.exceptions.HttpRequestException) {
        loggerError(error = e)
        null
    }
}


suspend inline fun <reified T : Any> supabase(
    crossinline operation: suspend () -> T?,
    failed: (String) -> Unit,
): T? {
    return try {
        kotlinx.coroutines.coroutineScope {
            operation()
        }
    } catch (e: io.github.jan.supabase.exceptions.RestException) {
        failed(e.stackTraceToString())
        loggerError(error = e)
        null
    } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
        failed(e.stackTraceToString())
        loggerError(error = e)
        null
    } catch (e: io.github.jan.supabase.exceptions.HttpRequestException) {
        failed(e.stackTraceToString())
        loggerError(error = e)
        null
    }
}

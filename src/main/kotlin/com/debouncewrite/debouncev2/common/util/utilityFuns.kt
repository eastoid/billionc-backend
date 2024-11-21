package com.debouncewrite.debouncev2.common.util

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import reactor.core.publisher.Mono
import java.time.Instant
import com.debouncewrite.debouncev2.common.model.Result


fun <T> Mono<T>.launch() {
    this.onErrorContinue { e, _ ->
        // Handle error here, e.g., logging the error
        println("Error occurred: ${e.message}")
    }.subscribe()
}


suspend fun <K, V> Flow<Map.Entry<K, V>>.collectMap(): Map<K, V> {
    val resultMap = mutableMapOf<K, V>()
    this.collect { entry ->
        resultMap[entry.key] = entry.value
    }
    return resultMap
}

inline fun <T> Boolean.ifTrue(func: () -> T): T? {
    if (this) return func()
    return null
}

inline fun <reified T> parseJsonOrNull(json: String): T? {
    try {
        return Json.decodeFromString<T>(json)
    } catch (e: Exception) {
        return null
    }
}

fun unixNow() = Instant.now().epochSecond

fun <T> T.print(vararg text: String): T {
    println(text.joinToString(" "))
    return this
}

fun <T> T.toResult(): Result<T> {
    return Result.ok(this)
}
package com.debouncewrite.debouncev2.common.util

import org.springframework.http.server.reactive.ServerHttpRequest
import java.time.Instant

object NetworkUtils {

    var lastLoggedError: Instant? = null
    fun getRealIp(request: ServerHttpRequest): String? {
        val header = request.headers["X-Forwarded-For"]
        if (header.isNullOrEmpty()) {
            val now = Instant.now()
            if (lastLoggedError == null || now.minusSeconds(120).isAfter(lastLoggedError)) {
                lastLoggedError = now
            }
            return null
        }

        val ip = header[0]
        return ip
    }

    fun getCensoredIp(request: ServerHttpRequest): String? {
        val ip = getRealIp(request) ?: return null

        val s = StringBuilder()
        var dot = false
        ip.forEach {
            if (dot) {
                s.append("*")
                return@forEach
            }

            if (it == '.') {
                dot = true
                return@forEach
            }

            s.append(it)
        }

        return s.toString()
    }

}
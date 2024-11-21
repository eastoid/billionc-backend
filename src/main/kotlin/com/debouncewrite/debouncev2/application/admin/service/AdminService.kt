package com.debouncewrite.debouncev2.application.admin.service

import com.debouncewrite.debouncev2.common.util.ULIDGenerator
import com.debouncewrite.debouncev2.common.util.unixNow
import io.netty.util.internal.ConcurrentSet
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class AdminService() {

    private val authTokens = ConcurrentHashMap<String, Long>()

    fun createAuthToken(): String {
        val now = unixNow()
        val token = "${ULIDGenerator.generate().toString()}${UUID.randomUUID()}${UUID.randomUUID()}"
        authTokens[token] = now
        return token
    }

    fun verifyAuthToken(token: String): Boolean {
        val now = unixNow()
        val creationDate = authTokens[token] ?: return false
        // 1 Week
        if ((now - creationDate) > 604800) {
            authTokens.remove(token)
            return false
        } else {
            return true
        }
    }

}
package com.debouncewrite.debouncev2.infrastructure.rsocket.service

import com.debouncewrite.debouncev2.infrastructure.rsocket.RsocketRateLimitConfig
import com.debouncewrite.debouncev2.infrastructure.rsocket.RsocketRateLimiter
import org.springframework.stereotype.Service

@Service
class RsocketRateLimitService {

    private val config = listOf(
        RsocketRateLimitConfig("con", 3, 1),
        RsocketRateLimitConfig("get", 40, 1),
        RsocketRateLimitConfig("set", 8, 1),
        RsocketRateLimitConfig("uns", 45, 1),
    )

    private val rateLimiter = RsocketRateLimiter(config)

    fun isRequestAllowed(ipAddress: String, route: String): Boolean {
        return rateLimiter.isAllowed(ipAddress, route)
    }

}
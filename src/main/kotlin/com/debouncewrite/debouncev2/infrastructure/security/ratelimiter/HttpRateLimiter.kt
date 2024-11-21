package com.debouncewrite.debouncev2.infrastructure.security.ratelimiter

import com.debouncewrite.debouncev2.infrastructure.security.EndpointSecurityConfigs
import java.util.concurrent.ConcurrentHashMap


class HttpRateLimiter {

    // Store rate limit state per IP and path ID
    private val userLimits = ConcurrentHashMap<String, ConcurrentHashMap<Int, RateLimitState>>()

    fun requestAllowed(ip: String, path: String): Boolean {
        // Get the configuration for the given path
        val config = EndpointSecurityConfigs.fromPath(path)
        val userRateLimits = userLimits.computeIfAbsent(ip) { ConcurrentHashMap() }
        val rateState = userRateLimits.computeIfAbsent(config.id) {
            RateLimitState(config.start.toLong(), System.currentTimeMillis(), 0)
        }

        val now = System.currentTimeMillis()

        synchronized(rateState) {
            // Check if user is still under penalty
            if (now < rateState.penaltyUntil) {
                return false
            }

            // Refill tokens based on elapsed time
            val elapsed = now - rateState.lastRefill
            if (elapsed > config.refillMs) {
                val tokensToAdd = elapsed / config.refillMs
                rateState.tokens = Math.min(
                    config.max.toLong(),
                    rateState.tokens + tokensToAdd
                )
                rateState.lastRefill += tokensToAdd * config.refillMs
            }

            // Allow request if tokens are available
            return if (rateState.tokens > 0) {
                rateState.tokens--
                true
            } else {
                // Apply penalty when no tokens remain
                rateState.penaltyUntil = now + config.penaltyMs
                false
            }
        }
    }

    // Internal state for a user's rate limiting on a specific path
    private data class RateLimitState(
        var tokens: Long,
        var lastRefill: Long,
        var penaltyUntil: Long
    )
}

package com.debouncewrite.debouncev2.infrastructure.rsocket

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


// Configuration class for rate limits per route
data class RsocketRateLimitConfig(val route: String, val maxRequests: Int, val perSeconds: Long)

// Rate limiter class to keep track of requests
class RsocketRateLimiter(private val rateLimits: List<RsocketRateLimitConfig>) {

    // Mapping for route to rate limit configuration
    private val rateLimitMap: Map<String, RsocketRateLimitConfig> = rateLimits.associateBy { it.route }

    // Track requests per IP + route with timestamp
    private val requestCounts = ConcurrentHashMap<String, RequestTracker>()

    // Data class to keep track of requests per IP and route
    private data class RequestTracker(
        val count: AtomicInteger = AtomicInteger(0),
        var lastResetTime: Instant = Instant.now()
    )

    fun isAllowed(ipAddress: String, route: String): Boolean {
        val config = rateLimitMap[route] ?: return true // No limit for unknown routes

        // Unique key for this IP and route
        val key = "$ipAddress:$route"
        val tracker = requestCounts.computeIfAbsent(key) { RequestTracker() }

        synchronized(tracker) {
            // Check if we need to reset the counter based on time
            val now = Instant.now()
            val secondsElapsed = java.time.Duration.between(tracker.lastResetTime, now).seconds
            if (secondsElapsed >= config.perSeconds) {
                tracker.count.set(0)
                tracker.lastResetTime = now
            }

            // Check if the request is within the allowed rate limit
            return if (tracker.count.incrementAndGet() <= config.maxRequests) {
                true
            } else {
                // If limit is exceeded, decrement the counter back
                tracker.count.decrementAndGet()
                false
            }
        }
    }
}
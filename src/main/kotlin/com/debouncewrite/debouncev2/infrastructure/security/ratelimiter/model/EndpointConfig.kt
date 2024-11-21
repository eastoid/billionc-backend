package com.debouncewrite.debouncev2.infrastructure.security.ratelimiter.model

data class EndpointConfig(
    val startingTokens: Int,
    val maxTokens: Int,
    val refillInterval: Int,
    val isAuthenticated: Boolean,
    val mode: RatelimitMode
)
package com.debouncewrite.debouncev2.infrastructure.security.ratelimiter.model

import com.debouncewrite.debouncev2.common.model.UserAction
import com.debouncewrite.debouncev2.application.user.model.Role

data class SecurityEndpointConfig(
    // Endpoint path
    val path: String,

    // Path ID - stored in memory instead of actual path string
    val id: Int,

    // Token refill rate (milliseconds per token)
    val refillMs: Int,

    // Max tokens
    val max: Int,

    // Starting tokens
    val start: Int,

    // How many milliseconds a user has to wait before being able to make requests
    // Applied after a ratelimit
    val penaltyMs: Long,

    val userAction: UserAction,

    val excludeLogging: Boolean = false,
) {


}

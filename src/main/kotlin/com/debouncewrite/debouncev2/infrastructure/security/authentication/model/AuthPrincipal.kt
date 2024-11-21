package com.debouncewrite.debouncev2.infrastructure.security.authentication.model

import com.debouncewrite.debouncev2.infrastructure.security.ratelimiter.model.SecurityEndpointConfig
import com.debouncewrite.debouncev2.application.user.model.Role
import com.debouncewrite.debouncev2.application.user.model.Status
import de.huxhorn.sulky.ulid.ULID
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

data class AuthPrincipal(
    val userId: ULID.Value?,
    val email: String?,
    val isAuthenticated: Boolean,
    val role: Role?,
    val requestIpAddress: String,
    val requestPath: SecurityEndpointConfig,
    val requestTime: Instant,
    val requestTimeMilli: Long,
    val mfaEnabled: Boolean,
    val status: Status?,
    val accountLockedUntil: Long?,
) {

    companion object {
        fun unauthenticated(ipAddress: String, path: SecurityEndpointConfig): AuthPrincipal {
            val now = Instant.now()
            return AuthPrincipal(
                null,
                null,
                false,
                null,
                ipAddress,
                path,
                now,
                now.toEpochMilli(),
                false,
                null,
                null,
            )
        }
    }

    init {
        if (isAuthenticated && userId == null) throw IllegalStateException("userId must not be null when isAuthenticated is true in AuthPrincipal")
    }

    val authenticatedUserId: ULID.Value
        get() = if (isAuthenticated) userId!! else throw IllegalStateException("AuthPrincipal was authenticated with ID null")

    fun serialize(): String {
        val map = mapOf<String, String>(
            "userId" to this.userId.toString(),
            "email" to this.email.toString(),
            "role" to this.role!!.name,
            "mfaEnabled" to this.mfaEnabled.toString(),
            "status" to this.status!!.name,
            "accountLockedUntil" to this.accountLockedUntil.toString(),
        )
        return Json.encodeToString(map)
    }

}
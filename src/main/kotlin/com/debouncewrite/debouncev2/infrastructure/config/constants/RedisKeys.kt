package com.debouncewrite.debouncev2.infrastructure.config.constants

import de.huxhorn.sulky.ulid.ULID
import de.huxhorn.sulky.ulid.ULID.Value

object RedisKeys {

    object RateLimiting {
        val prefix_bucket = "rlb"

        object Hash {
            val tokens = "tc"
            val rateLimits = "rlc"
            val lastRequest = "lr"
            val penaltyExpiry = "pe"
            val lastRatelimit = "lrl"
            val isPenalized = "p"
            val lastRefill = "lrf"
        }

        fun bucket(identifier: String, id: Int) = "$prefix_bucket:$identifier:$id"
    }

    object Security {
        private val prefix_blacklist = "bl"
        private val prefix_userIdByToken = "ubt"
        private val prefix_authPrincipal = "ap"
        private val prefix_totpSecret = "totps"
        private val prefix_passwordResetCount = "prescount"
        private val prefix_passwordResetCode = "prescode"
        private val prefix_mfaResetCode = "mfarescode"
        private val prefix_ipToken = "ipt"

        fun userIdByToken(token: String) = "$prefix_userIdByToken:$token"
        fun blacklistStatus(identifier: String) = "$prefix_blacklist:$identifier"
        fun authPrincipal(userId: ULID.Value) = "$prefix_authPrincipal:$userId"
        fun totpSecret(userId: Value) = "$prefix_totpSecret:$userId"
        fun passwordResetCount(email: String) = "$prefix_passwordResetCount:$email"
        fun passwordResetCode(email: String) = "$prefix_passwordResetCode:$email"
        fun mfaResetCode(email: String) = "$prefix_mfaResetCode:$email"
        fun ipToken(token: String) = "$prefix_mfaResetCode:$token"

        fun emailChangeCode(userId: ULID.Value, newEmail: String) = "$userId:$newEmail"

        object Hash {
            object Principal {
                val email = "el"
                val role = "re"
                val mfaStatus = "ms"
                val status = "ss"
                val accountLockedUntil = "al"
            }
        }
    }
}
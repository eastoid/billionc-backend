package com.debouncewrite.debouncev2.application.user.model

enum class Status {
    ACTIVE,
    BANNED,
    AUTOBANNED,
    UNVERIFIED;

    companion object {
        fun fromString(value: String?): Status? {
            value ?: return null
            return runCatching { valueOf(value) }.getOrElse { null }
        }
    }
}

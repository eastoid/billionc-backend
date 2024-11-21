package com.debouncewrite.debouncev2.infrastructure.config.constants

import io.micrometer.core.instrument.config.InvalidConfigurationException
import kotlin.system.exitProcess


object Properties {
    val homepageUrl = env("BILLIONC_URL_HOMEPAGE")
    val displayName = env("BILLIONC_TEXT_DISPLAYNAME")
    val port = env("BILLIONC_PORT").toIntOrNull() ?: run { abort("Application port must be a number."); 0 }

    object Secret {
        val adminTotpSecret = env("BILLIONC_ADMIN_TOTP_SECRET")
        val adminPassword = env("BILLIONC_ADMIN_PASSWORD")
    }

    private fun env(env: String): String {
        val result = System.getenv(env)
        if (result.isNullOrBlank()) {
            abort("Environment variable $env is not null or blank.")
        }

        return result
    }

    private fun abort(message: String = "No abort message provided. An environment variable was misconfigured.") {
        runCatching { throw InvalidConfigurationException("[Environment Variable Misconfiguration]: $message") }.exceptionOrNull()?.printStackTrace()
        exitProcess(1)
    }
}
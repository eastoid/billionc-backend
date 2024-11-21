package com.debouncewrite.debouncev2.common.util

import com.atlassian.onetime.core.TOTPGenerator
import com.atlassian.onetime.model.EmailAddress
import com.atlassian.onetime.model.Issuer
import com.atlassian.onetime.model.TOTPSecret
import com.atlassian.onetime.service.*

object TotpUtil {

    private val totpGenerator = TOTPGenerator()
    private val configuration = TOTPConfiguration()
    val service = DefaultTOTPService(totpGenerator = totpGenerator, totpConfiguration = configuration)

    fun generateTotp(secret: TOTPSecret): String {
        return totpGenerator.generateCurrent(secret).value
    }

    fun parseSecret(string: String): TOTPSecret {
        return TOTPSecret.fromBase32EncodedString(string)
    }

    fun generateSecret(): String {
        return RandomSecretProvider.generateSecret().base32Encoded
    }

    fun generatePair(email: String): Pair<String, String> {
        val secret = RandomSecretProvider.generateSecret()
        val url = service.generateTOTPUrl(secret, EmailAddress(email), Issuer("Debounce")).toString()
        return secret.base32Encoded to url
    }

    fun generateUrl(secretStr: String, email: String): String {
        val secret = parseSecret(secretStr)
        return service.generateTOTPUrl(secret, EmailAddress(email), Issuer("Debounce")).toString()
    }

    fun generateTotpForSecret(secret: String): String {
        val s = parseSecret(secret)
        return generateTotp(s)
    }

    fun verifyTotp(input: String, secret: String): Boolean {
        val actual = generateTotpForSecret(secret)
        return actual == input
    }
}
package com.debouncewrite.debouncev2.infrastructure.config.validation

import com.debouncewrite.debouncev2.common.util.StringUtils
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class ValidationService {

    val base64Decoder = Base64.getDecoder()
    val urlBase64Decoder = Base64.getUrlDecoder()

    /*
        BCrypt salt = 60
        BCrypt passwd hash = 60
        Plain random master key = 64
        Base64 master password = 43
        Base64 Encrypted master key = 108
        Base64 ChaCha20 Header = 32
     */

    fun emailValid(email: String): String? {
        if (email.isEmpty()) return "Email is empty."
        if (!email.contains("@")) return "Email does not contain @."

        val atIndex = email.indexOf("@")
        val dotIndex = email.lastIndexOf(".")
        if (dotIndex < atIndex) return "Email does not contain a valid domain."

        val domain = email.substringAfter("@")
        if (!domain.contains(".") || domain.startsWith(".") || domain.endsWith(".")) {
            return "Email does not contain a valid domain."
        }

        if (email.length < 5) return "Email is too short."
        if (email.length > 250) return "Email is too long."

//        val blockedEmailProviders = listOf("example.com", "blocked.com")
//        if (blockedEmailProviders.any { domain.equals(it, ignoreCase = true) }) return "Invalid email provider."

        val atCount = email.count { it == '@' }
        if (atCount != 1) return "Invalid email format."

        val body = email.substringBefore("@")
        if (body.isEmpty()) return "Email local part is empty."

        val firstChar = body.first()
        val lastChar = body.last()

        // Check for invalid characters in the local part
        var escaped = false
        for (char in body) {
            if (char == '\\') {
                escaped = !escaped
            } else {
                val isValidChar = char.isLetterOrDigit() || "!#$%&'*+/=?^_`{|}~.-".contains(char)
                if (!isValidChar && !escaped) {
                    return "Email contains invalid character ($char)."
                }
                escaped = false
            }
        }

        // Ensure no consecutive dots in local part
        if (body.contains("..")) return "Email cannot contain two dots consecutively."

        // Ensure local part does not start or end with special characters
        if (firstChar == '.' || lastChar == '.') return "Email cannot start or end with a dot."
        if (firstChar == '-' || lastChar == '-') return "Email cannot start or end with a hyphen."
        if (lastChar == '\\') return "Email cannot end with a backslash."

        // Ensure quoted string rules are respected
        val quotes = body.count { it == '"' }
        if (quotes == 2) {
            if (firstChar != '"' || lastChar != '"') return "Invalid quoted email."
        } else if (quotes != 0) return "Invalid quoted email."

        // Email length constraints
        if (body.length > 64) return "Local part of the email is too long."
        if (domain.length > 255) return "Domain part of the email is too long."

        return null
    }

    fun bCryptHashValid(hash: String?): String? {
        if (hash == null) return "Hash is null."
        if (hash.isBlank()) return "Hash is blank."
        if (
            !hash.startsWith("$2a$")
            && !hash.startsWith("$2b$")
            && !hash.startsWith("$2y$")
        ) return "Invalid BCrypt hash"
        if (hash.length != 60) return "Invalid BCrypt hash length"

        try {
            BCrypt.checkpw("", hash)
            return null
        } catch (e: Exception) {
            return "Invalid BCrypt hash"
        }
    }

    fun encryptedMasterKeyValid(key: String): String? {
        if (key.isBlank()) return "Encrypted master key is empty."
        if (key.length != 108) return "Encrypted master key has invalid length."
        try {
            urlBase64Decoder.decode(key)
        } catch (e: Exception) {
            e.printStackTrace()
            return "Encrypted master key is invalid (failed to decode Base64)."
        }
        return null
    }

    fun chaCha20HeaderValid(head: String): String? {
        if (head.isBlank()) return "Encryption header/iv is blank."
        if (head.length != 32) return "Encryption header/iv has invalid length."
        try {
            urlBase64Decoder.decode(head)
        } catch (e: Exception) {
            return "Encryption header/iv is invalid."
        }
        return null
    }

    fun bCryptSaltValid(salt: String?): String? {
        if (salt.isNullOrBlank()) return "BCrypt salt is empty."
        if (!salt.startsWith("$2a$") && !salt.startsWith("$2b$") && !salt.startsWith("$2y$")) return "BCrypt salt is invalid."
        if (salt.length != 29) return "BCrypt salt has invalid length."
        return null
    }

    fun totpValid(totp: String?): String? {
        if (totp.isNullOrBlank()) return "TOTP is empty."
        if (totp.length != 6) return "TOTP has invalid length."

        totp.forEach {
            if (!it.isDigit()) return "TOTP contains invalid character."
        }
        return null
    }

    fun pbkdf2HashValid(hash: String?): String? {
        if (hash.isNullOrBlank()) return "Hash is empty."
        if (hash.length != 43) return "Hash has invalid length."

        try {
            urlBase64Decoder.decode(hash)
        } catch (e: Exception) {
            return "Hash is invalid"
        }
        return null
    }

    val noteTitleRegex = Regex("^[a-zA-Z0-9-_ ]*\$")
    fun noteTitleValid(title: String?): String? {
        if (title.isNullOrBlank()) return "Title is empty."
        if (title.length > 128) return "Title is too long."

        val matches = noteTitleRegex.matches(title)
        if (!matches) return "Title contains invalid characters."

        return null
    }

    fun authtokenValid(authToken: String?): String? {
        if (authToken.isNullOrBlank()) return "Auth token is empty."
        if (authToken.length != 62) return "Auth token is invalid."
        return null
    }

    fun suggestionValid(sug: String?): String? {
        if (sug == null) return "Suggestion is null."
        if (sug.isBlank()) return "Suggestion is empty."
        if (sug.length < 3) return "Suggestion is too short."
        if (sug.length > 200) return "Suggestion is too long."
        return null
    }

    fun journalEntryValid(entry: String?): String? {
        if (entry == null) return "Entry is null."
        if (entry == "") return "Entry is empty."
        if (entry.length > 6670) return "Entry is too long."

        return null
    }

    fun mfaResetCodeValid(code: String?): String? {
        if (code == null) return "Reset code is null."
        if (code.isBlank()) return "Reset code is blank."
        if (code.length != 6) return "Reset code should be 8 characters long."
        return null
    }
}
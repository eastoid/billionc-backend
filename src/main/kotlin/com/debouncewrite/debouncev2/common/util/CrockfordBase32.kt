package com.debouncewrite.debouncev2.common.util

import com.debouncewrite.debouncev2.common.model.Result
import java.nio.charset.StandardCharsets

object CrockfordBase32 {

    private val CROCKFORD_BASE32_ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
    private val CROCKFORD_BASE32_VALUES = IntArray(256) { -1 }.apply {
        for (i in CROCKFORD_BASE32_ALPHABET.indices) {
            this[CROCKFORD_BASE32_ALPHABET[i].code] = i
        }
        // Handle both lowercase and uppercase letters
        for (i in 'a'..'z') {
            this[i.code] = this[i.uppercaseChar().code]
        }
    }

    fun decode(input: String): Result<ByteArray> {
        if (input.length < 2) return Result.error("Input for base32 decoding is too short.")

        val cleanedInput = input.filter { it.isLetterOrDigit() }.uppercase()
        val output = ByteArray((cleanedInput.length * 5 + 7) / 8)
        var buffer = 0
        var bitsLeft = 0
        var index = 0

        for (char in cleanedInput) {
            val value = CROCKFORD_BASE32_VALUES[char.code]
            if (value == -1) {
                return Result.error("Invalid character in Crockford Base32 string: $char")
            }

            buffer = (buffer shl 5) or value
            bitsLeft += 5

            if (bitsLeft >= 8) {
                output[index++] = (buffer shr (bitsLeft - 8)).toByte()
                bitsLeft -= 8
            }
        }

        // Check for remaining bits
        if (bitsLeft > 0) {
            if ((buffer and ((1 shl bitsLeft) - 1)) != 0) {
                return Result.error("Invalid padding in Crockford Base32 string")
            }
        }

        return Result.ok(output.copyOf(index))
    }

    fun encode(input: ByteArray): Result<String> {
        if (input.isEmpty()) return Result.error("Cannot Base32 encode empty ByteArray.")

        val stringBuilder = StringBuilder((input.size * 8 + 4) / 5)
        var buffer = 0
        var bitsLeft = 0

        for (byte in input) {
            buffer = (buffer shl 8) or (byte.toInt() and 0xFF)
            bitsLeft += 8

            while (bitsLeft >= 5) {
                stringBuilder.append(CROCKFORD_BASE32_ALPHABET[(buffer shr (bitsLeft - 5)) and 0x1F])
                bitsLeft -= 5
            }
        }

        if (bitsLeft > 0) {
            stringBuilder.append(CROCKFORD_BASE32_ALPHABET[(buffer shl (5 - bitsLeft)) and 0x1F])
        }

        return Result.ok(stringBuilder.toString())
    }

}

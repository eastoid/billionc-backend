package com.debouncewrite.debouncev2.common.util

import de.huxhorn.sulky.ulid.ULID

object ULIDGenerator {
    private val ulid = ULID()

    fun generate(): ULID.Value {
        return ulid.nextValue()
    }

    fun parse(str: String?): ULID.Value? {
        str ?: return null
        if (str.length != 26) return null
        return runCatching { ULID.parseULID(str) }.getOrNull()
    }
}
package com.debouncewrite.debouncev2.common.util

import java.time.Instant

object AtomicEpochNano {

    @Volatile
    private var lastNano = Instant.now().toEpochMilli()

    val epochNano: Long
        get() = synchronized(this) {
            getNano()
        }

    private fun getNano(): Long {
        val now = Instant.now()
        val nanos = now.epochSecond * 1_000_000_000 + now.nano

        if (nanos == lastNano) {
            return getNano()
        } else {
            lastNano = nanos
            return nanos
        }
    }
}

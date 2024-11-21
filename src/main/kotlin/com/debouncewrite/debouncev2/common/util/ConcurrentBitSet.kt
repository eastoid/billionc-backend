package com.debouncewrite.debouncev2.common.util

import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicLongArray

class ConcurrentBitSet(size: Int) {
    private val array = AtomicLongArray((size + 63) / 64) // calculate required number of Longs

    fun setBit(index: Int) {
        val elementIndex = index / 64
        val bitIndex = index % 64
        var oldValue: Long
        var newValue: Long
        do {
            oldValue = array[elementIndex]
            newValue = oldValue or (1L shl bitIndex)
        } while (!array.compareAndSet(elementIndex, oldValue, newValue))
    }

    fun clearBit(index: Int) {
        val elementIndex = index / 64
        val bitIndex = index % 64
        var oldValue: Long
        var newValue: Long
        do {
            oldValue = array[elementIndex]
            newValue = oldValue and (1L shl bitIndex).inv()
        } while (!array.compareAndSet(elementIndex, oldValue, newValue))
    }

    fun isBitSet(index: Int): Boolean {
        val elementIndex = index / 64
        val bitIndex = index % 64
        return (array[elementIndex] and (1L shl bitIndex)) != 0L
    }

    fun getBitRange(startIndex: Int, endIndex: Int): List<Boolean> {
        require(endIndex >= startIndex) { "End index must be >= start index" }
        val result = mutableListOf<Boolean>()
        for (i in startIndex..endIndex) {
            result.add(isBitSet(i))
        }
        return result
    }

    fun setAllBits() {
        for (i in 0 until array.length()) {
            array.set(i, 0L)
        }
    }

    fun setBitsFromByteArray(input: ByteArray) {
        val buffer = ByteBuffer.wrap(input)
        for (i in 0 until array.length()) {
            array.set(i, if (buffer.remaining() >= Long.SIZE_BYTES) buffer.long else 0L)
        }
    }

    val bytes
        get(): ByteArray {
            val buffer = ByteBuffer.allocate(array.length() * Long.SIZE_BYTES)
            for (i in 0 until array.length()) {
                buffer.putLong(array.get(i))
            }
            return buffer.array()
        }
}

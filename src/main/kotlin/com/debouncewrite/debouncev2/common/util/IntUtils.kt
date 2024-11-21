package com.debouncewrite.debouncev2.common.util

object IntUtils {
    fun getRandomNumber(min: Int, max: Int): Int {
        return (min..max).random()
    }
}
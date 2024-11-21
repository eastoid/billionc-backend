package com.debouncewrite.debouncev2.common.model

import jakarta.annotation.Nonnull

data class Result<T>(
    private val inputValue: T? = null,
    private val inputError: String? = null,
    private val inputNotFound: Boolean = false,
    private val rejectInput: String? = null
) {

    companion object {
        fun <T> ok(value: T): Result<T> = Result(inputValue = value)
        fun <T> error(message: String): Result<T> = Result(inputError = message)
        fun <T> notFound(): Result<T> = Result(inputNotFound = true)
        fun <T> reject(message: String): Result<T> = Result(rejectInput = message)
    }

    val hasError
        get() = inputError != null && inputValue == null

    val isSuccessful
        get() = inputValue != null && inputError == null && !inputNotFound && rejectInput == null

    val notFound
        get() = inputNotFound

    val rejected
        get() = rejectInput != null

    val value: T
        get() {
            if (inputValue == null) throw IllegalStateException("Tried to access empty value in result")
            return inputValue
        }

    val error: String
        get() {
            return inputError ?: rejectInput ?: throw IllegalStateException("Tried to access null error or rejection message in Result class.")
        }

    val valueOrNull: T?
        get() = inputValue
}
package com.rizkyizh.panopticon.helper

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error<out E>(val error: E) : Result<E>()
    object Loading : Result<Nothing>()

    data class ErrorMessage(val errorMessage: String): Result<Nothing>()
}
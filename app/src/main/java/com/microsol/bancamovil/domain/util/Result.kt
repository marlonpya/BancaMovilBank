package com.microsol.bancamovil.domain.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (exception: Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

fun <T> Result<T>.getDataOrNull(): T? = if (this is Result.Success) data else null
fun <T> Result<T>.getErrorOrNull(): Throwable? = if (this is Result.Error) exception else null
fun <T> Result<T>.isSuccess(): Boolean = this is Result.Success
fun <T> Result<T>.isError(): Boolean = this is Result.Error



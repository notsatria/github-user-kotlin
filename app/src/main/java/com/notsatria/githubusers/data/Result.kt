package com.notsatria.githubusers.data

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    data class Empty(val emptyError: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

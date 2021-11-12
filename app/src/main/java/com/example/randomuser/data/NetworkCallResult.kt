package com.example.randomuser.data

sealed class NetworkCallResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkCallResult<T>(data)
    class Error<T>(message: String?, data: T? = null) : NetworkCallResult<T>(data, message)
    class Loading<T> : NetworkCallResult<T>()
}


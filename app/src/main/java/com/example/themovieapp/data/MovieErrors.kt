package com.example.themovieapp.data

import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toMovieMessage(): String = when (this) {
    is UnknownHostException -> "No internet connection. Check your network and try again."
    is SocketTimeoutException -> "Request timed out. Please try again."
    is HttpException -> when (code()) {
        401 -> "Invalid API key. Check your TMDB configuration."
        404 -> "Not found. It may have been removed."
        429 -> "Too many requests. Wait a moment and try again."
        in 500..599 -> "Server error. Please try again later."
        else -> "Something went wrong (code ${code()}). Please try again."
    }
    is SerializationException -> "Bad response from server. Please try again."
    else -> message?.takeIf { it.isNotBlank() }
        ?: "Something went wrong. Please check your internet connection and try again."
}

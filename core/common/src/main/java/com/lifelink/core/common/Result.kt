package com.lifelink.core.common

/**
 * Universal wrapper returned by every Repository method in LifeLinkAI.
 * Every ViewModel exposes UI state derived from this, never a raw model directly,
 * so loading/error handling is consistent across all nine features.
 */
sealed class LifeLinkResult<out T> {
    data object Loading : LifeLinkResult<Nothing>()
    data class Success<T>(val data: T) : LifeLinkResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : LifeLinkResult<Nothing>()
}

inline fun <T, R> LifeLinkResult<T>.map(transform: (T) -> R): LifeLinkResult<R> = when (this) {
    is LifeLinkResult.Loading -> LifeLinkResult.Loading
    is LifeLinkResult.Success -> LifeLinkResult.Success(transform(data))
    is LifeLinkResult.Error -> LifeLinkResult.Error(message, cause)
}

inline fun <T> LifeLinkResult<T>.onSuccess(action: (T) -> Unit): LifeLinkResult<T> {
    if (this is LifeLinkResult.Success) action(data)
    return this
}

inline fun <T> LifeLinkResult<T>.onError(action: (String, Throwable?) -> Unit): LifeLinkResult<T> {
    if (this is LifeLinkResult.Error) action(message, cause)
    return this
}

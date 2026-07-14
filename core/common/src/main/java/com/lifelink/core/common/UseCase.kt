package com.lifelink.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Base class for a "read" use case that exposes a stream of results.
 * Keeping this generic and tiny on purpose — feature use cases extend it rather than
 * each repository leaking Room/Firestore types directly into ViewModels.
 */
abstract class FlowUseCase<in Params, out T> {
    operator fun invoke(params: Params): Flow<LifeLinkResult<T>> = flow {
        try {
            emit(LifeLinkResult.Loading)
            execute(params).collect { emit(LifeLinkResult.Success(it)) }
        } catch (e: Exception) {
            emit(LifeLinkResult.Error(e.message ?: "Unknown error", e))
        }
    }

    protected abstract fun execute(params: Params): Flow<T>
}

/** Base class for a one-shot "write" use case (add/update/delete/trigger). */
abstract class SuspendUseCase<in Params, out T> {
    suspend operator fun invoke(params: Params): LifeLinkResult<T> = try {
        LifeLinkResult.Success(execute(params))
    } catch (e: Exception) {
        LifeLinkResult.Error(e.message ?: "Unknown error", e)
    }

    protected abstract suspend fun execute(params: Params): T
}

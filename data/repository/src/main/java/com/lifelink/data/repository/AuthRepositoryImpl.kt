package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.remote.FirebaseAuthSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authSource: FirebaseAuthSource,
) : AuthRepository {

    override val currentUserId: String?
        get() = authSource.currentUser?.uid

    override suspend fun signIn(email: String, password: String): LifeLinkResult<Unit> = try {
        authSource.signInWithEmail(email, password)
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error(e.message ?: "Sign-in failed. Please check your credentials.", e)
    }

    override suspend fun register(email: String, password: String): LifeLinkResult<Unit> = try {
        authSource.registerWithEmail(email, password)
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error(e.message ?: "Registration failed.", e)
    }

    override fun signOut() = authSource.signOut()
}
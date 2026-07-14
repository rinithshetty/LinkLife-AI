package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult

interface AuthRepository {
    val currentUserId: String?
    suspend fun signIn(email: String, password: String): LifeLinkResult<Unit>
    suspend fun register(email: String, password: String): LifeLinkResult<Unit>
    fun signOut()
}
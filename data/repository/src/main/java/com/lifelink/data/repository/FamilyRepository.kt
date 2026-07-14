package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import kotlinx.coroutines.flow.Flow

data class FamilyMember(
    val id: String,
    val email: String,
    val nickname: String,
)

data class FamilyMemberStatus(
    val status: String,
    val timestampMillis: Long?,
)

interface FamilyRepository {
    fun observeFamilyMembers(): Flow<List<FamilyMember>>
    suspend fun addFamilyMember(email: String, nickname: String): LifeLinkResult<Unit>
    suspend fun removeFamilyMember(id: String): LifeLinkResult<Unit>
    suspend fun fetchMemberStatus(email: String): LifeLinkResult<FamilyMemberStatus?>
}
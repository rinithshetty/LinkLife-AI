package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.local.dao.FamilyMemberDao
import com.lifelink.data.local.entity.FamilyMemberEntity
import com.lifelink.data.remote.FirestoreSyncSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class FamilyRepositoryImpl @Inject constructor(
    private val dao: FamilyMemberDao,
    private val remote: FirestoreSyncSource,
) : FamilyRepository {

    override fun observeFamilyMembers(): Flow<List<FamilyMember>> =
        dao.observeAll().map { list -> list.map { FamilyMember(it.id, it.email, it.nickname) } }

    override suspend fun addFamilyMember(email: String, nickname: String): LifeLinkResult<Unit> = try {
        dao.insert(
            FamilyMemberEntity(
                id = UUID.randomUUID().toString(),
                email = email.trim(),
                nickname = nickname.trim(),
                addedAtMillis = System.currentTimeMillis(),
            ),
        )
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not add family member.", e)
    }

    override suspend fun removeFamilyMember(id: String): LifeLinkResult<Unit> = try {
        dao.deleteById(id)
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not remove family member.", e)
    }

    override suspend fun fetchMemberStatus(email: String): LifeLinkResult<FamilyMemberStatus?> = try {
        val data = remote.fetchPublicCheckIn(email)
        val status = data?.get("status") as? String
        val timestamp = (data?.get("timestampMillis") as? Number)?.toLong()
        LifeLinkResult.Success(if (status != null) FamilyMemberStatus(status, timestamp) else null)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not check their status — they may not have checked in yet, or you're offline.", e)
    }
}
package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import com.lifelink.data.local.dao.CheckInDao
import com.lifelink.data.local.entity.CheckInEntity
import com.lifelink.data.remote.FirebaseAuthSource
import com.lifelink.data.remote.FirestoreSyncSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class CheckInRepositoryImpl @Inject constructor(
    private val dao: CheckInDao,
    private val remote: FirestoreSyncSource,
    private val authSource: FirebaseAuthSource,
) : CheckInRepository {

    override fun observeLatestCheckIn(): Flow<SafetyCheckIn?> =
        dao.observeLatest().map { it?.toDomain() }

    override suspend fun recordCheckIn(
        status: SafetyStatus,
        latitude: Double?,
        longitude: Double?,
    ): LifeLinkResult<Unit> = try {
        dao.insert(
            CheckInEntity(
                id = UUID.randomUUID().toString(),
                status = status.name,
                latitude = latitude,
                longitude = longitude,
                timestampMillis = System.currentTimeMillis(),
            ),
        )
        LifeLinkResult.Success(Unit)
    } catch (e: Exception) {
        LifeLinkResult.Error("Could not record check-in.", e)
    }

    override suspend fun publishStatusPublicly(status: SafetyStatus): LifeLinkResult<Unit> {
        val email = authSource.currentUser?.email
            ?: return LifeLinkResult.Error("You need to be signed in to share your status with family.")
        return try {
            remote.uploadPublicCheckIn(
                email = email,
                data = mapOf(
                    "status" to status.name,
                    "timestampMillis" to System.currentTimeMillis(),
                ),
            )
            LifeLinkResult.Success(Unit)
        } catch (e: Exception) {
            LifeLinkResult.Error("Could not share your status — check your connection and try again.", e)
        }
    }
}

private fun CheckInEntity.toDomain() = SafetyCheckIn(
    id = id,
    status = runCatching { SafetyStatus.valueOf(status) }.getOrDefault(SafetyStatus.UNKNOWN),
    latitude = latitude,
    longitude = longitude,
    timestampMillis = timestampMillis,
)
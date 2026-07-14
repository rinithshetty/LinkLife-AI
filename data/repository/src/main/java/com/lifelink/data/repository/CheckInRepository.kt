package com.lifelink.data.repository

import com.lifelink.core.common.LifeLinkResult
import kotlinx.coroutines.flow.Flow

enum class SafetyStatus { SAFE, NEEDS_HELP, UNKNOWN }

data class SafetyCheckIn(
    val id: String,
    val status: SafetyStatus,
    val latitude: Double?,
    val longitude: Double?,
    val timestampMillis: Long,
)

interface CheckInRepository {
    fun observeLatestCheckIn(): Flow<SafetyCheckIn?>
    suspend fun recordCheckIn(status: SafetyStatus, latitude: Double?, longitude: Double?): LifeLinkResult<Unit>
    suspend fun publishStatusPublicly(status: SafetyStatus): LifeLinkResult<Unit>
}

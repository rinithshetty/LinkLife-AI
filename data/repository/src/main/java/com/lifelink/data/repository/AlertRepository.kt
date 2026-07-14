package com.lifelink.data.repository

import kotlinx.coroutines.flow.Flow

data class DisasterAlert(
    val id: String,
    val title: String,
    val message: String,
    val severity: String,
    val disasterType: String,
    val timestampMillis: Long,
    val isRead: Boolean,
)

interface AlertRepository {
    fun observeAlerts(): Flow<List<DisasterAlert>>
    suspend fun saveAlert(alert: DisasterAlert)
    suspend fun markRead(id: String)
}
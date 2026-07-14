package com.lifelink.data.repository

import com.lifelink.data.local.dao.AlertDao
import com.lifelink.data.local.entity.AlertEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val dao: AlertDao,
) : AlertRepository {

    override fun observeAlerts(): Flow<List<DisasterAlert>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveAlert(alert: DisasterAlert) {
        dao.insert(alert.toEntity())
    }

    override suspend fun markRead(id: String) {
        dao.markRead(id)
    }
}

private fun AlertEntity.toDomain() = DisasterAlert(id, title, message, severity, disasterType, timestampMillis, isRead)

private fun DisasterAlert.toEntity() = AlertEntity(id, title, message, severity, disasterType, timestampMillis, isRead)
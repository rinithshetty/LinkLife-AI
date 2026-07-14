package com.lifelink.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lifelink.data.local.dao.GuideDao
import com.lifelink.data.local.entity.GuideEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Guides are the clearest example of "offline by construction": they are seeded from
 * bundled JSON assets into Room at first launch (see feature:guides/assets) and read
 * exclusively from Room afterward — there is no network path for this feature at all.
 */
class GuideRepositoryImpl @Inject constructor(
    private val dao: GuideDao,
) : GuideRepository {

    private val gson = Gson()
    private val stepsListType = object : TypeToken<List<String>>() {}.type

    override fun observeGuides(disasterType: String?): Flow<List<EmergencyGuide>> {
        val flow = if (disasterType == null) dao.observeAll() else dao.observeByType(disasterType)
        return flow.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun seedIfEmpty(guides: List<EmergencyGuide>) {
        if (dao.count() == 0) {
            dao.insertAll(
                guides.mapIndexed { index, guide ->
                    GuideEntity(
                        id = guide.id,
                        disasterType = guide.disasterType,
                        title = guide.title,
                        stepsJson = gson.toJson(guide.steps),
                        orderIndex = index,
                    )
                },
            )
        }
    }

    private fun GuideEntity.toDomain() = EmergencyGuide(
        id = id,
        disasterType = disasterType,
        title = title,
        steps = gson.fromJson(stepsJson, stepsListType),
    )
}

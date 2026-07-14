package com.lifelink.data.local.dao

import androidx.room.*
import com.lifelink.data.local.entity.GuideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuideDao {
    @Query("SELECT * FROM emergency_guides ORDER BY disasterType, orderIndex ASC")
    fun observeAll(): Flow<List<GuideEntity>>

    @Query("SELECT * FROM emergency_guides WHERE disasterType = :type ORDER BY orderIndex ASC")
    fun observeByType(type: String): Flow<List<GuideEntity>>

    @Query("SELECT COUNT(*) FROM emergency_guides")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(guides: List<GuideEntity>)
}

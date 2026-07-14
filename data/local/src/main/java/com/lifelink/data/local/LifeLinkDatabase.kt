package com.lifelink.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lifelink.data.local.dao.AlertDao
import com.lifelink.data.local.dao.CheckInDao
import com.lifelink.data.local.dao.ContactDao
import com.lifelink.data.local.dao.FamilyMemberDao
import com.lifelink.data.local.dao.GuideDao
import com.lifelink.data.local.dao.ReminderDao
import com.lifelink.data.local.dao.VaultDao
import com.lifelink.data.local.entity.AlertEntity
import com.lifelink.data.local.entity.CheckInEntity
import com.lifelink.data.local.entity.ContactEntity
import com.lifelink.data.local.entity.FamilyMemberEntity
import com.lifelink.data.local.entity.GuideEntity
import com.lifelink.data.local.entity.ReminderEntity
import com.lifelink.data.local.entity.VaultRecordEntity

@Database(
    entities = [
        ContactEntity::class,
        ReminderEntity::class,
        VaultRecordEntity::class,
        GuideEntity::class,
        CheckInEntity::class,
        AlertEntity::class,
        FamilyMemberEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class LifeLinkDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun reminderDao(): ReminderDao
    abstract fun vaultDao(): VaultDao
    abstract fun guideDao(): GuideDao
    abstract fun checkInDao(): CheckInDao
    abstract fun alertDao(): AlertDao
    abstract fun familyMemberDao(): FamilyMemberDao

    companion object {
        const val DATABASE_NAME = "lifelink.db"
    }
}
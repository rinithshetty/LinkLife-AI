package com.lifelink.data.local.di

import android.content.Context
import androidx.room.Room
import com.lifelink.data.local.LifeLinkDatabase
import com.lifelink.data.local.dao.AlertDao
import com.lifelink.data.local.dao.CheckInDao
import com.lifelink.data.local.dao.ContactDao
import com.lifelink.data.local.dao.FamilyMemberDao
import com.lifelink.data.local.dao.GuideDao
import com.lifelink.data.local.dao.ReminderDao
import com.lifelink.data.local.dao.VaultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LifeLinkDatabase =
        Room.databaseBuilder(context, LifeLinkDatabase::class.java, LifeLinkDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideContactDao(db: LifeLinkDatabase): ContactDao = db.contactDao()

    @Provides
    fun provideReminderDao(db: LifeLinkDatabase): ReminderDao = db.reminderDao()

    @Provides
    fun provideVaultDao(db: LifeLinkDatabase): VaultDao = db.vaultDao()

    @Provides
    fun provideGuideDao(db: LifeLinkDatabase): GuideDao = db.guideDao()

    @Provides
    fun provideCheckInDao(db: LifeLinkDatabase): CheckInDao = db.checkInDao()

    @Provides
    fun provideAlertDao(db: LifeLinkDatabase): AlertDao = db.alertDao()

    @Provides
    fun provideFamilyMemberDao(db: LifeLinkDatabase): FamilyMemberDao = db.familyMemberDao()
}
package com.lifelink.data.repository.di

import com.lifelink.data.repository.AlertRepository
import com.lifelink.data.repository.AlertRepositoryImpl
import com.lifelink.data.repository.AuthRepository
import com.lifelink.data.repository.AuthRepositoryImpl
import com.lifelink.data.repository.CheckInRepository
import com.lifelink.data.repository.CheckInRepositoryImpl
import com.lifelink.data.repository.ContactRepository
import com.lifelink.data.repository.ContactRepositoryImpl
import com.lifelink.data.repository.FamilyRepository
import com.lifelink.data.repository.FamilyRepositoryImpl
import com.lifelink.data.repository.GuideRepository
import com.lifelink.data.repository.GuideRepositoryImpl
import com.lifelink.data.repository.HospitalRepository
import com.lifelink.data.repository.HospitalRepositoryImpl
import com.lifelink.data.repository.ReminderRepository
import com.lifelink.data.repository.ReminderRepositoryImpl
import com.lifelink.data.repository.VaultRepository
import com.lifelink.data.repository.VaultRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): ContactRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindVaultRepository(impl: VaultRepositoryImpl): VaultRepository

    @Binds
    @Singleton
    abstract fun bindGuideRepository(impl: GuideRepositoryImpl): GuideRepository

    @Binds
    @Singleton
    abstract fun bindCheckInRepository(impl: CheckInRepositoryImpl): CheckInRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHospitalRepository(impl: HospitalRepositoryImpl): HospitalRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository

    @Binds
    @Singleton
    abstract fun bindFamilyRepository(impl: FamilyRepositoryImpl): FamilyRepository
}
package com.lifelink.data.remote.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lifelink.data.remote.PlacesApiService
import com.lifelink.data.remote.PlacesConfig
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Distinguishes the Places-specific OkHttpClient/Retrofit instances from core:ai's
 * Gemini-specific ones. Without this, both modules provide unqualified OkHttpClient and
 * Retrofit into the same Hilt component, which Dagger correctly rejects as a duplicate
 * binding — Hilt has no way to know which one a given injection site wants.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlacesHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlacesRetrofit

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providePlacesConfig(): PlacesConfig =
        PlacesConfig(apiKey = com.lifelink.data.remote.BuildConfig.MAPS_API_KEY)

    @Provides
    @Singleton
    @PlacesHttpClient
    fun providePlacesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @PlacesRetrofit
    fun providePlacesRetrofit(@PlacesHttpClient client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://places.googleapis.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providePlacesApiService(@PlacesRetrofit retrofit: Retrofit): PlacesApiService =
        retrofit.create(PlacesApiService::class.java)
}
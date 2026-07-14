package com.lifelink.core.ai.di

import com.lifelink.core.ai.GeminiApiService
import com.lifelink.core.ai.GeminiConfig
import com.lifelink.core.ai.GeminiRepository
import com.lifelink.core.ai.GeminiRepositoryImpl
import com.lifelink.core.common.DefaultDispatcherProvider
import com.lifelink.core.common.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiBindsModule {
    @Binds
    @Singleton
    abstract fun bindGeminiRepository(impl: GeminiRepositoryImpl): GeminiRepository

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}

@Module
@InstallIn(SingletonComponent::class)
object AiProvidesModule {

    @Provides
    @Singleton
    fun provideGeminiConfig(): GeminiConfig =
        // NEVER hardcode a real key here. Wired from BuildConfig, which reads
        // GEMINI_API_KEY out of local.properties (gitignored) at build time.
        // See app/build.gradle.kts buildConfigField wiring instructions in README.
        GeminiConfig(apiKey = com.lifelink.core.ai.BuildConfig.GEMINI_API_KEY)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideGeminiApiService(retrofit: Retrofit): GeminiApiService =
        retrofit.create(GeminiApiService::class.java)
}

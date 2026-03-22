package com.microsol.bancamovil.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.microsol.bancamovil.BuildConfig
import com.microsol.bancamovil.data.remote.api.AuthService
import com.microsol.bancamovil.data.remote.api.MovementsService
import com.microsol.bancamovil.data.remote.api.ProductsService
import com.microsol.bancamovil.data.remote.interceptor.AuthInterceptor
import com.microsol.bancamovil.data.remote.mock.MockAuthService
import com.microsol.bancamovil.data.remote.mock.MockMovementsService
import com.microsol.bancamovil.data.remote.mock.MockProductsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor = AuthInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://fxservicesstaging.nunchee.com/api/1.0/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // Mock
    @Provides
    @Singleton
    fun provideAuthService(mockAuthService: MockAuthService): AuthService = mockAuthService

    @Provides
    @Singleton
    fun provideProductsService(mockProductsService: MockProductsService): ProductsService = mockProductsService

    @Provides
    @Singleton
    fun provideMovementsService(mockMovementsService: MockMovementsService): MovementsService = mockMovementsService
}

package com.microsol.bancamovil.di

import com.microsol.bancamovil.data.repository.AuthRepositoryImpl
import com.microsol.bancamovil.data.repository.ProductsRepositoryImpl
import com.microsol.bancamovil.data.repository.MovementsRepositoryImpl
import com.microsol.bancamovil.domain.repository.AuthRepository
import com.microsol.bancamovil.domain.repository.ProductsRepository
import com.microsol.bancamovil.domain.repository.MovementsRepository
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ): ProductsRepository

    @Binds
    @Singleton
    abstract fun bindMovementsRepository(
        movementsRepositoryImpl: MovementsRepositoryImpl
    ): MovementsRepository
}


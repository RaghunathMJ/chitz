package com.example.chitassist.data.di

import com.example.chitassist.data.repository.ChitRepositoryImpl
import com.example.chitassist.data.repository.UserRepositoryImpl
import com.example.chitassist.domain.repository.ChitRepository
import com.example.chitassist.domain.repository.UserRepository
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
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindChitRepository(
        chitRepositoryImpl: ChitRepositoryImpl
    ): ChitRepository
}

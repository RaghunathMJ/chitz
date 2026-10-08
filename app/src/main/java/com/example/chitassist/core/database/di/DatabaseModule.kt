package com.example.chitassist.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.chitassist.core.database.ChitDatabase
import com.example.chitassist.core.database.dao.ChitDao
import com.example.chitassist.core.database.dao.PaymentDao
import com.example.chitassist.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChitDatabase {
        return Room.databaseBuilder(
            context,
            ChitDatabase::class.java,
            ChitDatabase.DATABASE_NAME
        ).addCallback(ChitDatabase.CALLBACK)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: ChitDatabase): UserDao = database.userDao()

    @Provides
    fun provideChitDao(database: ChitDatabase): ChitDao = database.chitDao()

    @Provides
    fun providePaymentDao(database: ChitDatabase): PaymentDao = database.paymentDao()
}

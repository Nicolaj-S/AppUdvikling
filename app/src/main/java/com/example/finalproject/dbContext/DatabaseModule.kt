package com.example.finalproject.dbContext

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): DbContext =
            Room.databaseBuilder(context, DbContext::class.java, "Android DB").build()

        @Provides
        fun provideUserDao(appDatabase: DbContext) = appDatabase.userDao()!!
    }
}
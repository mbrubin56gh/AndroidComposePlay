package com.example.sampletakehome.dependencygraph

import android.content.Context
import androidx.room.Room
import com.example.sampletakehome.database.UsersDatabase
import dagger.Module
import dagger.Provides


@Module
class ApplicationModule(private val applicationContext: Context) {
    @ApplicationScope
    @Provides
    fun providesUsersDatabase(): UsersDatabase = Room.databaseBuilder(
        applicationContext,
        UsersDatabase::class.java, "users-database"
    ).build()
}
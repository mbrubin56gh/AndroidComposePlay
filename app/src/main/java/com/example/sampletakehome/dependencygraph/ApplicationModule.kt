package com.example.sampletakehome.dependencygraph

import android.content.Context
import androidx.room.Room
import com.example.sampletakehome.database.UsersDatabase
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides


@Module
@ContributesTo(AppScope::class)
class ApplicationModule(private val applicationContext: Context) {
    @SingleIn(AppScope::class)
    @Provides
    fun providesUsersDatabase(): UsersDatabase = Room.databaseBuilder(
        applicationContext,
        UsersDatabase::class.java,
        "users-database"
    ).build()
}
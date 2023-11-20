package com.example.sampletakehome.dependencygraph

import android.content.Context
import androidx.room.Room
import com.example.sampletakehome.database.UsersDatabase
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides


@Module
@ContributesTo(AppScope::class)
class ApplicationModule {
    @SingleIn(AppScope::class)
    @Provides
    fun providesUsersDatabase(@ApplicationContext context: Context): UsersDatabase =
        Room.databaseBuilder(
            context,
            UsersDatabase::class.java,
            "users-database"
        ).build()
}
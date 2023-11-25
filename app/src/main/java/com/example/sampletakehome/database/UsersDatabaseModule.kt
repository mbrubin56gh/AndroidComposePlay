package com.example.sampletakehome.database

import android.content.Context
import androidx.room.Room
import com.example.sampletakehome.dependencygraph.AppScope
import com.example.sampletakehome.dependencygraph.ApplicationContext
import com.example.sampletakehome.dependencygraph.SingleIn
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppScope::class)
object UsersDatabaseModule {
    @SingleIn(AppScope::class)
    @Provides
    fun providesUsersDatabase(@ApplicationContext context: Context): UsersDatabase =
        Room.databaseBuilder(
            context,
            UsersDatabase::class.java,
            "users-database"
        ).build()

    @Provides
    fun providesUsersDao(usersDatabase: UsersDatabase): UsersDao = usersDatabase.userDao()
}
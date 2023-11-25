package com.example.sampletakehome.repository

import com.example.sampletakehome.database.UsersDao
import com.example.sampletakehome.database.UsersDatabase
import com.example.sampletakehome.dependencygraph.AppScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppScope::class)
object UsersRepositoryModule {
    @Provides
    fun providesUsersDao(usersDatabase: UsersDatabase): UsersDao = usersDatabase.userDao()
}
package com.example.sampletakehome.repository

import com.example.sampletakehome.networking.User
import com.example.sampletakehome.networking.UsersService
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

sealed class UsersResponse {
    class Success(val users: List<User>) : UsersResponse()
    object Error : UsersResponse()
}

class UsersRepository @Inject constructor(private val usersService: UsersService) {
    suspend fun users(): UsersResponse {
        return try {
            UsersResponse.Success(usersService.users().users)
        } catch (e: Exception) {
            logcat { e.asLog() }
            UsersResponse.Error
        }
    }
}
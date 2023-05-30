package com.example.sampletakehome.repository

import com.example.sampletakehome.networking.Networking.usersService
import com.example.sampletakehome.networking.User
import logcat.asLog
import logcat.logcat
import java.lang.Exception

sealed class UsersResponse {
    class Success(val users: List<User>) : UsersResponse()
    object Error : UsersResponse()
}

object UsersRepository {
    suspend fun users(): UsersResponse {
        return try {
            UsersResponse.Success(usersService.users().users)
        } catch (e: Exception) {
            logcat { e.asLog() }
            UsersResponse.Error
        }
    }
}
package com.example.sampletakehome.repository

import com.example.sampletakehome.User
import com.example.sampletakehome.networking.Networking.usersService
import com.example.sampletakehome.toUsers
import logcat.asLog
import logcat.logcat

sealed class UsersResponse {
    class Success(val users: List<User>) : UsersResponse()
    object Error : UsersResponse()
}

object UsersRepository {
    suspend fun users(): UsersResponse {
        return try {
            UsersResponse.Success(usersService.users().users.toUsers())
        } catch (e: Exception) {
            logcat { e.asLog() }
            UsersResponse.Error
        }
    }
}
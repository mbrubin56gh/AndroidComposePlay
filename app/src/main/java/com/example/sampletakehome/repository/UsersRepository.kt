package com.example.sampletakehome.repository

import com.example.sampletakehome.User
import com.example.sampletakehome.database.UserEntity
import com.example.sampletakehome.database.UsersDatabase
import com.example.sampletakehome.networking.UserNetworkModel
import com.example.sampletakehome.networking.UsersService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val usersService: UsersService, private val usersDatabase: UsersDatabase
) {
    suspend fun refreshUsers() {
        usersDatabase.userDao().insertAll(usersService.users().users.toUserEntities())
    }

    fun users(): Flow<List<User>> = usersDatabase.userDao().getAll().map { it.toUsers() }

    suspend fun getUser(userId: Long) = usersDatabase.userDao().getOne(userId).toUser()
}

fun UserEntity.toUser(): User = User(id = id, firstName = firstName, imageUrl = imageUrl)

fun List<UserEntity>.toUsers(): List<User> = map { it.toUser() }

fun UserNetworkModel.toUserEntity() = UserEntity(
    id = id,
    firstName = firstName,
    imageUrl = imageUrl
)

fun List<UserNetworkModel>.toUserEntities() = map { it.toUserEntity() }

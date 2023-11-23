package com.example.sampletakehome.repository

import com.example.sampletakehome.database.UserEntity
import com.example.sampletakehome.database.UsersDao
import com.example.sampletakehome.networking.UserNetworkModel
import com.example.sampletakehome.networking.UsersService
import com.example.sampletakehome.repository.UsersRepository.UsersResult.Success
import com.example.sampletakehome.repository.UsersRepository.UsersResult.WithNetworkError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class User(
    val id: Long,
    val firstName: String,
    val imageUrl: String
)

class UsersRepository @Inject constructor(
    private val usersService: UsersService,
    private val usersDao: UsersDao
) {
    sealed class UsersResult {
        data object NotInitialized : UsersResult()
        data class WithNetworkError(val users: List<User>) : UsersResult()
        data class Success(val users: List<User>) : UsersResult()
    }

    private var networkError = false

    suspend fun refreshUsers() {
        try {
            val users = usersService.users()
            networkError = false
            usersDao.insertAll(users.users.toUserEntities())
        } catch (e: Exception) {
            networkError = true
        }
    }

    fun users(): Flow<UsersResult> = usersDao.getAll()
        .map {
            it.toUsers()
                .let { users -> if (networkError) WithNetworkError(users) else Success(users) }
        }

    suspend fun getUser(userId: Long) = usersDao.getOne(userId).toUser()
}

fun UserEntity.toUser(): User = User(id = id, firstName = firstName, imageUrl = imageUrl)

fun List<UserEntity>.toUsers() = map { it.toUser() }

fun UserNetworkModel.toUserEntity() = UserEntity(
    id = id,
    firstName = firstName,
    imageUrl = imageUrl
)

fun List<UserNetworkModel>.toUserEntities() = map { it.toUserEntity() }

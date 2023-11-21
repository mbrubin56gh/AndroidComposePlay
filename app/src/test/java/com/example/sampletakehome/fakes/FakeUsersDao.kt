package com.example.sampletakehome.fakes

import com.example.sampletakehome.database.UserEntity
import com.example.sampletakehome.database.UsersDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeUsersDao(private val users: List<UserEntity>? = null) : UsersDao {
    private val userEntities = users?.toMutableList() ?: mutableListOf()

    override fun getAll(): Flow<List<UserEntity>> = flow {
        emit(userEntities)
    }

    override suspend fun insertAll(users: List<UserEntity>) {
        userEntities.addAll(users)
    }

    override suspend fun getOne(id: Long): UserEntity = userEntities.first { it.id == id }
}

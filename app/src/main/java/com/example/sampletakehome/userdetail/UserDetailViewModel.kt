package com.example.sampletakehome.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sampletakehome.User
import com.example.sampletakehome.repository.UsersRepository

class UserDetailViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    suspend fun getUser(userId: Long): User = usersRepository.getUser(userId)

    class Factory(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserDetailViewModel(usersRepository) as T
        }
    }
}

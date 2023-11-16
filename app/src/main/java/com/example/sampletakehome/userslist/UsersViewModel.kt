package com.example.sampletakehome.userslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sampletakehome.User
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.repository.UsersRepository.UsersResult
import com.example.sampletakehome.userslist.UsersUIState.Fetched
import com.example.sampletakehome.userslist.UsersUIState.Fetching
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UsersUIState {
    object Fetching : UsersUIState()
    sealed class Fetched : UsersUIState() {
        abstract val users: List<User>

        class Success(override val users: List<User>) : Fetched()
        class Error(override val users: List<User>) : Fetched()
    }
}

class UsersViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {
    private val _usersUiState: MutableStateFlow<UsersUIState> = MutableStateFlow(Fetching)
    val usersUiState = _usersUiState.asStateFlow()

    init {
        seedUsers()
    }

    suspend fun refreshUsers() = usersRepository.refreshUsers()

    suspend fun getUser(userId: Long): User = usersRepository.getUser(userId)

    private fun seedUsers() {
        viewModelScope.launch {
            usersRepository.refreshUsers()
            usersRepository.users().collect { usersResult ->
                _usersUiState.value = when (usersResult) {
                    is UsersResult.Success -> Fetched.Success(usersResult.users)
                    is UsersResult.WithNetworkError -> Fetched.Error(usersResult.users)
                }
            }
        }
    }

    class Factory(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return UsersViewModel(usersRepository) as T
        }
    }
}

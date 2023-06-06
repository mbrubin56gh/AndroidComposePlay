package com.example.sampletakehome.userslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.sampletakehome.User
import com.example.sampletakehome.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import logcat.logcat

class UsersViewModel(
    private val usersRepository: UsersRepository,
) : ViewModel() {
    sealed class UsersUIState {
        object Fetching : UsersUIState()
        sealed class Fetched : UsersUIState() {
            class Success(val users: List<User>) : UsersUIState()
            object Error : UsersUIState()
        }
    }

    private val _usersUiState: MutableStateFlow<UsersUIState> =
        MutableStateFlow(UsersUIState.Fetching)
    val usersUiState = _usersUiState.asStateFlow()

    init {
        viewModelScope.launch {
            usersRepository.users().collect { users ->
                _usersUiState.value = UsersUIState.Fetched.Success(users)
            }
        }
        viewModelScope.launch { refreshUsers() }
    }

    suspend fun refreshUsers() {
        // Should do something special with network errors: if we have data from the db,
        // then don't show an error state and just log, send to analytics, etc. Otherwise, do
        // show it. We could do something like below. Starting to get a bit state-machine like,
        // so could go in its own method implementing an all-in-one location for state machines
        // or use Square's Workflow, etc.
        try {
            usersRepository.refreshUsers()
        } catch (e: Exception) {
            if (_usersUiState.value is UsersUIState.Fetching) {
                _usersUiState.value = UsersUIState.Fetched.Error
            } else {
                logcat { "Error refreshing users." }
            }
        }
    }

    suspend fun getUser(userId: Long): User = usersRepository.getUser(userId)

    class Factory(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return UsersViewModel(usersRepository) as T
        }
    }
}

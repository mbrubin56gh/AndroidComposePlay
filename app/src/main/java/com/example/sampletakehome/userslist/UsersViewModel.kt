package com.example.sampletakehome.userslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sampletakehome.User
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.userslist.UsersViewModel.UsersUIState.Fetched
import com.example.sampletakehome.userslist.UsersViewModel.UsersUIState.Fetching
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import logcat.logcat

class UsersViewModel(private val usersRepository: UsersRepository) : ViewModel() {
    sealed class UsersUIState {
        object Fetching : UsersUIState()
        sealed class Fetched : UsersUIState() {
            class Success(val users: List<User>) : UsersUIState()
            object Error : UsersUIState()
        }
    }

    private val _usersUiState: MutableStateFlow<UsersUIState> = MutableStateFlow(Fetching)
    val usersUiState = _usersUiState.asStateFlow()

    private val _selectedUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val selectedUser = _selectedUser.asStateFlow()

    init {
        viewModelScope.launch {
            usersRepository.users().collect { users ->
                _usersUiState.value = Fetched.Success(users)
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
            if (_usersUiState.value is Fetching) {
                _usersUiState.value = Fetched.Error
            } else {
                logcat { "Error refreshing users." }
            }
        }
    }

    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun onSelectedUserHandled() {
        _selectedUser.value = null
    }

    class Factory(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UsersViewModel(usersRepository) as T
        }
    }
}
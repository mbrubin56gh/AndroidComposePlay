package com.example.sampletakehome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.repository.UsersResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersViewModel(usersRepository: UsersRepository) : ViewModel() {
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
            _usersUiState.value = when (val users = usersRepository.users()) {
                UsersResponse.Error -> UsersUIState.Fetched.Error
                is UsersResponse.Success -> UsersUIState.Fetched.Success(users.users)
            }
        }
    }

    class Factory(private val usersRepository: UsersRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UsersViewModel(usersRepository) as T
        }
    }
}
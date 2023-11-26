package com.example.sampletakehome.circuits

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.sampletakehome.R
import com.example.sampletakehome.dependencygraph.AppScope
import com.example.sampletakehome.generalui.User
import com.example.sampletakehome.repository.User
import com.example.sampletakehome.repository.UsersRepository
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailScreen(val userId: Long) : Screen {
    sealed interface State : CircuitUiState {
        data object Loading : State
        data class HasUser(val user: User) : State
    }
}

class UserDetailPresenter @AssistedInject constructor(
    @Assisted val screen: UserDetailScreen,
    private val usersRepository: UsersRepository
) : Presenter<UserDetailScreen.State> {

    @CircuitInject(UserDetailScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(screen: UserDetailScreen): UserDetailPresenter
    }

    @Composable
    override fun present(): UserDetailScreen.State {
        val user by produceState<User?>(initialValue = null) {
            value = usersRepository.getUser(screen.userId)
        }
        return user?.let { UserDetailScreen.State.HasUser(it) } ?: UserDetailScreen.State.Loading
    }
}

@CircuitInject(UserDetailScreen::class, AppScope::class)
@Composable
fun UserDetail(state: UserDetailScreen.State, modifier: Modifier = Modifier) {
    when (state) {
        is UserDetailScreen.State.HasUser -> User(modifier = modifier, user = state.user)
        UserDetailScreen.State.Loading -> UserDetailProgressIndicator()
    }
}

@Composable
fun UserDetailProgressIndicator(modifier: Modifier = Modifier) {
    val content = stringResource(R.string.loading_user_content_description)
    CircularProgressIndicator(modifier = modifier.semantics { contentDescription = content })
}

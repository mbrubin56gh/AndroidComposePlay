package com.example.sampletakehome.circuits

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sampletakehome.R
import com.example.sampletakehome.circuits.UsersScreen.Event
import com.example.sampletakehome.dependencygraph.AppScope
import com.example.sampletakehome.generalui.User
import com.example.sampletakehome.generalui.UsersRefresher
import com.example.sampletakehome.repository.User
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.repository.UsersRepository.UsersResult
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.parcelize.Parcelize

@Parcelize
data object UsersScreen : Screen {
    sealed interface State : CircuitUiState {
        data object Fetching : State
        sealed class Fetched : State {
            abstract val users: List<User>
            abstract val isRefreshing: Boolean
            abstract val eventSink: (Event) -> Unit

            class Success(
                override val users: List<User>,
                override val isRefreshing: Boolean,
                override val eventSink: (Event) -> Unit
            ) : Fetched()

            class Error(
                override val users: List<User>,
                override val isRefreshing: Boolean,
                override val eventSink: (Event) -> Unit
            ) : Fetched()
        }
    }

    sealed interface Event : CircuitUiEvent {
        data object RefreshUsers : Event
        data class UserSelected(val userId: Long) : Event
    }
}

class UsersPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator, private val usersRepository: UsersRepository
) : Presenter<UsersScreen.State> {
    @CircuitInject(UsersScreen::class, AppScope::class)
    @AssistedFactory
    fun interface Factory {
        fun create(navigator: Navigator): UsersPresenter
    }

    @Composable
    override fun present(): UsersScreen.State {
        var isRefreshing by rememberRetained { mutableStateOf(false) }
        if (isRefreshing) {
            LaunchedEffect(Unit) {
                usersRepository.refreshUsers()
                isRefreshing = false
            }
        }

        var users: UsersResult by rememberRetained { mutableStateOf(UsersResult.NotInitialized) }
        if (users is UsersResult.NotInitialized) {
            LaunchedEffect(Unit) {
                usersRepository.refreshUsers()
                usersRepository.users().collect { users = it }
            }
        }

        return when (val usersResult = users) {
            UsersResult.NotInitialized -> UsersScreen.State.Fetching

            is UsersResult.Success -> UsersScreen.State.Fetched.Success(
                users = usersResult.users, isRefreshing = isRefreshing
            ) {
                when (it) {
                    is Event.RefreshUsers -> isRefreshing = true
                    is Event.UserSelected -> navigator.goTo(UserDetailScreen(it.userId))
                }
            }

            is UsersResult.WithNetworkError -> UsersScreen.State.Fetched.Error(
                users = usersResult.users, isRefreshing = isRefreshing
            ) {
                when (it) {
                    is Event.RefreshUsers -> isRefreshing = true
                    is Event.UserSelected -> navigator.goTo(UserDetailScreen(it.userId))
                }
            }
        }
    }
}

@CircuitInject(UsersScreen::class, AppScope::class)
@Composable
fun UsersUi(
    state: UsersScreen.State, modifier: Modifier = Modifier
) {
    when (state) {
        is UsersScreen.State.Fetching -> FetchingProgressBar(modifier)
        is UsersScreen.State.Fetched -> {
            val userList = @Composable {
                UsersList(
                    modifier = modifier,
                    users = state.users,
                    isRefreshing = state.isRefreshing,
                    eventSink = state.eventSink
                )
            }
            when (state) {
                is UsersScreen.State.Fetched.Error -> if (state.users.isNotEmpty()) {
                    userList()
                } else {
                    FetchErrorMessage(
                        modifier = modifier,
                        isRefreshing = state.isRefreshing,
                        eventSink = state.eventSink
                    )
                }

                is UsersScreen.State.Fetched.Success -> userList()
            }
        }
    }
}

@Composable
fun UsersList(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    users: List<User> = emptyList(),
    eventSink: (Event) -> Unit
) {
    UsersRefresher(modifier = modifier,
        isRefreshing = isRefreshing,
        makeScrollable = false,
        onRefresh = { eventSink(Event.RefreshUsers) }) {
        LazyColumn {
            itemsIndexed(items = users, key = { _, user -> user.id }) { index, user ->
                User(modifier = modifier,
                    user = user,
                    onUserClicked = { id -> eventSink(Event.UserSelected(id)) })
                if (index < users.size - 1) {
                    Divider(
                        color = Color.Gray, thickness = 1.dp, modifier = Modifier.alpha(.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun FetchErrorMessage(
    modifier: Modifier = Modifier, isRefreshing: Boolean = false, eventSink: (Event) -> Unit
) {
    UsersRefresher(modifier = modifier,
        makeScrollable = true,
        isRefreshing = isRefreshing,
        onRefresh = { eventSink(Event.RefreshUsers) }) {
        Text(
            modifier = modifier.align(Alignment.Center),
            text = stringResource(R.string.error_fetching_contacts),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun FetchingProgressBar(modifier: Modifier = Modifier) {
    val content = stringResource(R.string.loading_users_content_description)
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier
                .align(Alignment.Center)
                .semantics { contentDescription = content })
    }
}

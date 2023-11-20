package com.example.sampletakehome.userslist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sampletakehome.R
import com.example.sampletakehome.SampleUsersApplication.Companion.applicationComponent
import com.example.sampletakehome.User
import com.example.sampletakehome.dependencygraph.AppScope
import com.example.sampletakehome.repository.UsersRepository
import com.example.sampletakehome.repository.UsersRepository.UsersResult
import com.example.sampletakehome.theme.MyApplicationTheme
import com.example.sampletakehome.userslist.UsersActivity.UserDetailScreen
import com.example.sampletakehome.userslist.UsersActivity.UsersScreen
import com.example.sampletakehome.userslist.UsersActivity.UsersScreen.Event
import com.example.sampletakehome.userslist.UsersActivity.UsersScreen.Event.RefreshUsers
import com.example.sampletakehome.userslist.UsersActivity.UsersScreen.State.Fetched
import com.example.sampletakehome.userslist.UsersActivity.UsersScreen.State.Fetching
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.parcelize.Parcelize


class UsersActivity : ComponentActivity() {
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
        @Assisted private val navigator: Navigator,
        private val usersRepository: UsersRepository
    ) : Presenter<UsersScreen.State> {
        @CircuitInject(UsersScreen::class, AppScope::class)
        @AssistedFactory
        fun interface Factory {
            fun create(navigator: Navigator): UsersPresenter
        }

        @Composable
        override fun present(): UsersScreen.State {
            var isRefreshing by rememberSaveable { mutableStateOf(false) }
            if (isRefreshing) {
                LaunchedEffect(Unit) {
                    usersRepository.refreshUsers()
                    isRefreshing = false
                }
            }

            val users by produceState<UsersResult?>(initialValue = null) {
                usersRepository.refreshUsers()
                usersRepository.users().collect { value = it }
            }
            return when (val usersResult = users) {
                null -> Fetching
                is UsersResult.Success -> Fetched.Success(
                    users = usersResult.users,
                    isRefreshing = isRefreshing
                ) {
                    when (it) {
                        is RefreshUsers -> isRefreshing = true
                        is Event.UserSelected -> navigator.goTo(UserDetailScreen(it.userId))
                    }
                }

                is UsersResult.WithNetworkError -> Fetched.Error(
                    users = usersResult.users,
                    isRefreshing = isRefreshing
                ) {
                    when (it) {
                        is RefreshUsers -> isRefreshing = true
                        is Event.UserSelected -> navigator.goTo(UserDetailScreen(it.userId))
                    }
                }
            }
        }
    }

    @Parcelize
    data class UserDetailScreen(val userId: Long) : Screen {
        class State(val user: User?) : CircuitUiState
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
            return UserDetailScreen.State(user)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val backstack = rememberSaveableBackStack { push(UsersScreen) }
                val navigator = rememberCircuitNavigator(backstack)
                CircuitCompositionLocals(applicationComponent.circuit()) {
                    NavigableCircuitContent(
                        navigator = navigator,
                        backstack = backstack
                    )
                }
            }
        }
    }
}

@CircuitInject(UsersScreen::class, AppScope::class)
@Composable
fun UsersUi(
    state: UsersScreen.State,
    modifier: Modifier = Modifier
) {
    when (state) {
        is Fetching -> FetchingProgressBar(modifier)
        is Fetched -> {
            val userList = @Composable {
                UsersList(
                    modifier = modifier,
                    users = state.users,
                    isRefreshing = state.isRefreshing,
                    eventSink = state.eventSink
                )
            }
            when (state) {
                is Fetched.Error -> if (state.users.isNotEmpty()) {
                    userList()
                } else {
                    FetchErrorMessage(
                        modifier = modifier,
                        isRefreshing = state.isRefreshing,
                        eventSink = state.eventSink
                    )
                }

                is Fetched.Success -> userList()
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
    UsersRefresher(
        modifier = modifier,
        isRefreshing = isRefreshing,
        makeScrollable = false,
        onRefresh = { eventSink(RefreshUsers) }
    ) {
        LazyColumn {
            itemsIndexed(items = users, key = { _, user -> user.id }) { index, user ->
                User(
                    modifier = modifier,
                    user = user,
                    onUserClicked = { id -> eventSink(Event.UserSelected(id)) }
                )
                if (index < users.size - 1) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.alpha(.5f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersRefresher(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    makeScrollable: Boolean = false,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val refreshState = rememberPullRefreshState(isRefreshing, {
        onRefresh()
    })

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
            .then(if (makeScrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
    ) {
        content()

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = refreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}

@CircuitInject(UserDetailScreen::class, AppScope::class)
@Composable
fun UserDetail(state: UserDetailScreen.State, modifier: Modifier = Modifier) {
    state.user?.let { User(modifier = modifier, user = it) } ?: CircularProgressIndicator()
}

@Composable
fun User(
    modifier: Modifier = Modifier,
    user: User,
    onUserClicked: (Long) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 96.dp)
            .clickable { onUserClicked(user.id) }
            .padding(vertical = 4.dp),
        verticalAlignment = CenterVertically,
    ) {
        UserImage(
            modifier = modifier.align(CenterVertically),
            url = requireNotNull(user.imageUrl) { "Image url was null" })
        Spacer(modifier = modifier.width(12.dp))
        UserLabel(modifier = modifier.align(CenterVertically), name = user.firstName)
    }
}

@Composable
fun UserImage(
    modifier: Modifier = Modifier,
    url: String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(url).crossfade(true).build(),
        placeholder = ColorPainter(Color.Gray),
        contentDescription = stringResource(R.string.user_image_content_description),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .padding(4.dp)
            .clip(CircleShape)
            .fillMaxHeight()
            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = true)
    )
}

@Composable
fun UserLabel(
    modifier: Modifier = Modifier,
    name: String
) {
    Text(
        modifier = modifier,
        text = name,
        style = MaterialTheme.typography.displaySmall
    )
}

@Composable
fun FetchErrorMessage(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    eventSink: (Event) -> Unit
) {
    UsersRefresher(
        modifier = modifier,
        makeScrollable = true,
        isRefreshing = isRefreshing,
        onRefresh = { eventSink(RefreshUsers) }
    ) {
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
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier.align(Alignment.Center))
    }
}

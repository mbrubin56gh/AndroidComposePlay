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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sampletakehome.R
import com.example.sampletakehome.SampleUsersApplication.Companion.applicationComponent
import com.example.sampletakehome.User
import com.example.sampletakehome.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class UsersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val viewModel = viewModel<UsersViewModel>(
                    factory = UsersViewModel.Factory(applicationComponent.usersRepository())
                )
                val navController = rememberNavController()
                Navigation(
                    modifier = Modifier,
                    navController = navController,
                    usersUIState = viewModel.usersUiState.collectAsStateWithLifecycle().value,
                    getUser = viewModel::getUser,
                    onSelectedUser = { user ->
                        navController.navigate(Routes.User.pathFromUserId(user.id))
                    },
                    onRefreshUsers = viewModel::refreshUsers
                )
            }
        }
    }
}

private sealed class Routes {
    abstract val routeName: String
    abstract val arguments: List<NamedNavArgument>

    object Users : Routes() {
        override val routeName = "user"
        override val arguments: List<NamedNavArgument> = emptyList()
    }

    object User : Routes() {
        private const val USERID_PATH = "userId"

        override val routeName = "user/{$USERID_PATH}"
        override val arguments = listOf(navArgument(USERID_PATH) { type = NavType.LongType })
        fun userId(backStackEntry: NavBackStackEntry): Long {
            return checkNotNull(backStackEntry.arguments?.getLong(USERID_PATH)) {
                "Missing userId from backstack"
            }
        }

        fun pathFromUserId(userId: Long): String = "user/$userId"
    }
}

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    usersUIState: UsersUIState,
    onRefreshUsers: suspend () -> Unit,
    onSelectedUser: (User) -> Unit,
    getUser: suspend (Long) -> User
) {
    NavHost(navController = navController, startDestination = Routes.Users.routeName) {
        composable(route = Routes.Users.routeName, arguments = Routes.Users.arguments) {
            Users(
                modifier = modifier,
                usersUIState = usersUIState,
                onRefreshUsers = onRefreshUsers,
                onSelectedUser = onSelectedUser
            )
        }
        composable(Routes.User.routeName, Routes.User.arguments) { backStackEntry ->
            UserDetail(
                userId = Routes.User.userId(backStackEntry),
                getUser = getUser
            )
        }
    }
}

@Composable
fun Users(
    modifier: Modifier = Modifier,
    usersUIState: UsersUIState,
    onRefreshUsers: suspend () -> Unit,
    onSelectedUser: (User) -> Unit
) {
    when (usersUIState) {
        UsersUIState.Fetching -> FetchingProgressBar(modifier)
        is UsersUIState.Fetched -> {
            val userList = @Composable {
                UsersList(
                    modifier = modifier,
                    users = usersUIState.users,
                    onUserClicked = onSelectedUser,
                    refreshUsers = onRefreshUsers
                )
            }
            when (usersUIState) {
                is UsersUIState.Fetched.Error -> if (usersUIState.users.isNotEmpty()) {
                    userList()
                } else {
                    FetchErrorMessage(modifier, onRefreshUsers)
                }

                is UsersUIState.Fetched.Success -> userList()
            }
        }
    }
}

@Composable
fun UsersList(
    modifier: Modifier = Modifier,
    users: List<User> = emptyList(),
    onUserClicked: (User) -> Unit,
    refreshUsers: suspend () -> Unit
) {
    UsersRefresher(modifier = modifier, refreshUsers = refreshUsers) {
        LazyColumn {
            itemsIndexed(items = users, key = { _, user -> user.id }) { index, user ->
                User(
                    modifier = modifier,
                    user = user,
                    onUserClicked = onUserClicked
                )
                if (index < users.size - 1) {
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.alpha(.5f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersRefresher(
    modifier: Modifier = Modifier,
    makeScrollable: Boolean = false,
    refreshUsers: suspend () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullRefreshState(refreshing, {
        refreshScope.launch {
            refreshing = true
            refreshUsers()
            refreshing = false
        }
    })

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
            .then(if (makeScrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
    ) {
        content()

        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun UserDetail(
    userId: Long,
    getUser: suspend (Long) -> User
) {
    val user by produceState<User?>(initialValue = null, key1 = userId) {
        value = getUser(userId)
    }
    user?.let { User(user = it) } ?: CircularProgressIndicator()
}

@Composable
fun User(
    modifier: Modifier = Modifier,
    user: User,
    onUserClicked: (User) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 96.dp)
            .clickable { onUserClicked(user) }
            .padding(vertical = 4.dp),
        verticalAlignment = CenterVertically,
    ) {
        UserImage(modifier = modifier.align(CenterVertically), url = requireNotNull(user.imageUrl) { "Image url was null" })
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
    onRefreshUsers: suspend () -> Unit
) {
    UsersRefresher(refreshUsers = onRefreshUsers, makeScrollable = true) {
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

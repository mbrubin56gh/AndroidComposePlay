package com.example.sampletakehome.userslist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sampletakehome.R
import com.example.sampletakehome.SampleUsersApplication.Companion.applicationComponent
import com.example.sampletakehome.User
import com.example.sampletakehome.theme.MyApplicationTheme
import com.example.sampletakehome.userslist.UsersViewModel.UsersUIState
import kotlinx.coroutines.launch

class UsersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Users(Modifier)
                }
            }
        }
    }
}

@Composable
fun Users(
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = viewModel(
        factory = UsersViewModel.Factory(applicationComponent.usersRepository())
    )
) {
    val userUiState by viewModel.usersUiState.collectAsStateWithLifecycle()
    val selectedUser by viewModel.selectedUser.collectAsStateWithLifecycle()
    when (val state = userUiState) {
        UsersUIState.Fetched.Error -> FetchErrorMessage(modifier)
        UsersUIState.Fetching -> FetchingProgressBar(modifier)
        is UsersUIState.Fetched.Success -> {
            // Baby self-handling of navigation. Really, integrating the Compose navigation
            // library or some alternative would be a better option.
            val userSelected = selectedUser != null
            AnimatedVisibility(visible = userSelected, enter = fadeIn(), exit = fadeOut()) {
                UserDetail(
                    modifier = modifier,
                    user = selectedUser,
                    onBack = viewModel::onSelectedUserDismissed
                )
            }
            AnimatedVisibility(visible = !userSelected, enter = fadeIn(), exit = fadeOut()) {
                UsersList(
                    modifier = modifier,
                    users = state.users,
                    onUserClicked = { user -> viewModel.selectUser(user) },
                    refreshUsers = viewModel::refreshUsers
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersList(
    modifier: Modifier = Modifier,
    users: List<User> = emptyList(),
    onUserClicked: (User) -> Unit,
    refreshUsers: suspend () -> Unit
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
    ) {
        LazyColumn(modifier = modifier) {
            items(items = users, key = { it.id }) { user ->
                User(modifier = modifier, user = user, onUserClicked = onUserClicked)
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = refreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun UserDetail(
    modifier: Modifier = Modifier,
    user: User?,
    onBack: () -> Unit
) {
    // Baby, home-grown navigation. Really, this should be handled by the the Compose navigation
    // library or one of the alternatives.
    BackHandler(enabled = user != null, onBack = onBack)
    user?.let {
        Box(modifier = modifier.fillMaxSize()) {
            User(user = user)
        }
    }
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
            .clickable { onUserClicked(user) },
        verticalAlignment = CenterVertically,
    ) {
        UserImage(
            modifier = modifier,
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
fun FetchErrorMessage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
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

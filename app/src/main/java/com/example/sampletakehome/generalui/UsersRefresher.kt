package com.example.sampletakehome.generalui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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

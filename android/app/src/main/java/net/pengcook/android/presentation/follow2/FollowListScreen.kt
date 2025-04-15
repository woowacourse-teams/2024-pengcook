package net.pengcook.android.presentation.follow2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.pengcook.android.R
import net.pengcook.android.presentation.core.components.TopBarWithNavigation
import net.pengcook.android.presentation.follow2.components.CustomAlertDialog
import net.pengcook.android.presentation.follow2.components.FollowInfoList
import net.pengcook.android.presentation.follow2.components.FollowSearchBar
import net.pengcook.android.presentation.follow2.components.SelectTabRow
import net.pengcook.android.presentation.follow2.model.FollowInfo
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun FollowListScreenRoot(
    viewModel: FollowListViewModel,
    navigateBack: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    FollowListScreen(
        state = state.value,
        onAction = { action ->
            when (action) {
                FollowListAction.NavigateBack -> navigateBack()
                else -> viewModel.onAction(action)
            }
        },
    )
}

@Composable
fun FollowListScreen(
    state: FollowListState,
    onAction: (FollowListAction) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    Scaffold(
        topBar = {
            TopBarWithNavigation(
                name = state.username,
                navigation = { onAction(FollowListAction.NavigateBack) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            SelectTabRow(
                selectedTabIndex = state.selectedTabIndex,
                onTabSelect = onAction,
                followerCount = state.followerCount,
                followingCount = state.followingCount,
            )

            FollowSearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = {
                },
                onImeSearch = {
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            if (state.selectedTabIndex == 0) {
                FollowInfoList(
                    followers = state.followers,
                    onButtonClick = { onAction(FollowListAction.ShowDialog(it)) },
                    isMine = state.isMine,
                    isFollowerInfo = true,
                )
            } else {
                FollowInfoList(
                    followers = state.followings,
                    onButtonClick = { onAction(FollowListAction.ShowDialog(it)) },
                    isMine = state.isMine,
                    isFollowerInfo = false,
                )
            }

            if (state.showDialog && state.selectedFollowInfo != null) {
                println("show dialog : ${state.selectedTabIndex}")
                if (state.selectedTabIndex == 0) {
                    CustomAlertDialog(
                        title = stringResource(R.string.alert_title_remove_follower),
                        description = stringResource(R.string.alert_description_remove_follower),
                        onClickCancel = { onAction(FollowListAction.HideDialog) },
                        onClickConfirm = {
                            onAction(FollowListAction.OnDeleteFollower(state.selectedFollowInfo.userId))
                        },
                    )
                } else {
                    CustomAlertDialog(
                        title = stringResource(R.string.alert_title_unfollow),
                        description = stringResource(R.string.alert_description_unfollow),
                        onClickCancel = { onAction(FollowListAction.HideDialog) },
                        onClickConfirm = {
                            onAction(FollowListAction.OnUnfollow(state.selectedFollowInfo.userId))
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun FollowListScreenPreview() {
    val followers = List(20) {
        FollowInfo(
            userId = it.toLong(),
            profileImageUrl = "https://randomuser.me/api/portraits",
            username = "Username",
        )
    }
    PengCookTheme {
        FollowListScreen(
            state = FollowListState(
                isMine = true,
                username = "Username",
                followers = followers,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun FollowListScreenPreviewNotMine() {
    val followers = List(20) {
        FollowInfo(
            userId = it.toLong(),
            profileImageUrl = "https://randomuser.me/api/portraits",
            username = "Username",
        )
    }
    PengCookTheme {
        FollowListScreen(
            state = FollowListState(
                isMine = false,
                username = "Username",
                followers = followers,
            ),
            onAction = {},
        )
    }
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FollowListScreenPreviewDarkMode() {
    val followers = List(20) {
        FollowInfo(
            userId = it.toLong(),
            profileImageUrl = "https://randomuser.me/api/portraits",
            username = "Username",
        )
    }

    PengCookTheme {
        FollowListScreen(
            state = FollowListState(
                isMine = true,
                username = "Username",
                followers = followers,
            ),
            onAction = {},
        )
    }
}

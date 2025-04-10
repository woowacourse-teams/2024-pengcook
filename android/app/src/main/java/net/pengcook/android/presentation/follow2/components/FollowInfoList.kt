package net.pengcook.android.presentation.follow2.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.presentation.follow2.model.FollowInfo
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun FollowInfoList(
    followers: List<FollowInfo>,
    onButtonClick: (Long) -> Unit,
    isMine: Boolean,
    isFollowerInfo: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(followers) { follower ->
            FollowInfoItem(
                followInfo = follower,
                modifier = Modifier.padding(horizontal = 16.dp),
                actionContent = {
                    if (isMine) {
                        FollowActionButton(
                            onButtonClick = onButtonClick,
                            follower = follower,
                            isFollowerInfo = isFollowerInfo,
                        )
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowersListPreview() {
    val followers = List(10) {
        FollowInfo(
            userId = it.toLong(),
            profileImageUrl = "https://randomuser.me/api/portraits",
            username = "Username",
        )
    }
    PengCookTheme {
        FollowInfoList(
            followers = followers,
            onButtonClick = {},
            isMine = true,
            isFollowerInfo = true,
        )
    }
}

@Preview(showBackground = true, uiMode = 32)
@Composable
private fun FollowersListPreviewDarkMode() {
    val followers = List(10) {
        FollowInfo(
            userId = it.toLong(),
            profileImageUrl = "https://randomuser.me/api/portraits",
            username = "Username",
        )
    }
    PengCookTheme {
        FollowInfoList(
            followers = followers,
            onButtonClick = {},
            isMine = true,
            isFollowerInfo = true,
        )
    }
}

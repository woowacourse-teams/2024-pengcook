package net.pengcook.android.presentation.follow2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.presentation.core.components.UrlImage
import net.pengcook.android.presentation.follow2.model.FollowInfo

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
                onButtonClick = { onButtonClick(follower.userId) },
                isMine = isMine,
                isFollowerInfo = isFollowerInfo,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun FollowInfoItem(
    followInfo: FollowInfo,
    onButtonClick: () -> Unit,
    isMine: Boolean,
    isFollowerInfo: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UrlImage(
            imageUrl = followInfo.profileImageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier.size(40.dp)
                .clip(CircleShape)
                .background(Color.White, shape = CircleShape)
                .border(0.5.dp, Color.Black, shape = CircleShape),
        )
        Text(
            followInfo.username,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
                .padding(start = 12.dp),
        )
        if (isMine) {
            Button(
                onClick = { onButtonClick() },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F7)),
            ) {
                Text(
                    text =
                        if (isFollowerInfo) {
                            "Remove"
                        } else {
                            "Unfollow"
                        },
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
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
    FollowInfoList(
        followers = followers,
        onButtonClick = {},
        isMine = true,
        isFollowerInfo = true,
    )
}

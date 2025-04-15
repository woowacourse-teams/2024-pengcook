package net.pengcook.android.presentation.follow2.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.R
import net.pengcook.android.presentation.follow2.model.FollowInfo

@Composable
fun FollowActionButton(
    onButtonClick: (Long) -> Unit,
    follower: FollowInfo,
    isFollowerInfo: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onButtonClick(follower.userId) },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.height(40.dp),
    ) {
        Text(
            text = if (isFollowerInfo) {
                stringResource(R.string.follower_remove)
            } else {
                stringResource(R.string.follower_unfollow)
            },
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview
@Composable
private fun FollowActionButtonPreview() {
    FollowActionButton(
        onButtonClick = {},
        follower = FollowInfo(
            userId = 1L,
            profileImageUrl = "https://randomuser.me/api/portraits",
            username = "Username",
        ),
        isFollowerInfo = true,
    )
}

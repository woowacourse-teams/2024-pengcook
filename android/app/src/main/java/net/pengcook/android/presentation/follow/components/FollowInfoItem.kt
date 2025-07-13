package net.pengcook.android.presentation.follow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.presentation.core.components.UrlImage
import net.pengcook.android.presentation.follow.model.FollowInfo
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun FollowInfoItem(
    followInfo: FollowInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionContent: @Composable RowScope.() -> Unit = {},
) {
    val theme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UrlImage(
            imageUrl = followInfo.profileImageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = theme.background, shape = CircleShape)
                .border(
                    0.5.dp,
                    color = theme.onPrimary,
                    shape = CircleShape,
                ),
        )
        Text(
            followInfo.username,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )

        actionContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun FollowInfoItemPreview() {
    val followInfo = FollowInfo(
        userId = 1L,
        profileImageUrl = "https://randomuser.me/api/portraits",
        username = "Username",
    )
    
    PengCookTheme {
        FollowInfoItem(
            followInfo = followInfo,
            onClick = {},
            actionContent = {
                Text(
                    text = "Follow",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp),
                )
            },
        )
    }
}

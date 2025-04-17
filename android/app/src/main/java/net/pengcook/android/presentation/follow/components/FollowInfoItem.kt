package net.pengcook.android.presentation.follow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.presentation.core.components.UrlImage
import net.pengcook.android.presentation.follow.model.FollowInfo

@Composable
fun FollowInfoItem(
    followInfo: FollowInfo,
    modifier: Modifier = Modifier,
    actionContent: @Composable RowScope.() -> Unit = {},
) {
    val theme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
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

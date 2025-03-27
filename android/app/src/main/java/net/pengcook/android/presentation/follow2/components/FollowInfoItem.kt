package net.pengcook.android.presentation.follow2.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.presentation.core.components.UrlImage
import net.pengcook.android.presentation.follow2.model.FollowInfo

@Composable
fun FollowInfoItem(
    followInfo: FollowInfo,
    modifier: Modifier = Modifier,
    actionContent: @Composable RowScope.(FollowInfo) -> Unit = {},
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
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White, shape = CircleShape)
                .border(0.5.dp, Color.Black, shape = CircleShape),
        )
        Text(
            followInfo.username,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )

        actionContent(followInfo)
    }
}

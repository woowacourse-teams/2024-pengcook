package net.pengcook.android.presentation.otherprofile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun ButtonRow(
    isFollowing: Boolean = false,
    onFollowClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        RoundedButton(
            text = if (isFollowing) "Unfollow" else "Follow",
            onClick = { onFollowClick() },
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        RoundedButton(
            text = "Block User",
            onClick = { onBlockClick() },
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButtons() {
    PengCookTheme {
        ButtonRow(
            isFollowing = true,
            onFollowClick = {},
            onBlockClick = {},
        )
    }
}

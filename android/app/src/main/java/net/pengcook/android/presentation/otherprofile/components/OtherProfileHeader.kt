package net.pengcook.android.presentation.otherprofile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.components.UrlImage
import net.pengcook.android.ui.theme.Notosans
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun OtherProfileHeader(
    userProfile: UserProfile?,
    onFollowListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = userProfile?.nickname ?: "",
                    style = Notosans.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )

                if (userProfile?.introduction != null && userProfile.introduction.isNotEmpty()) {
                    Text(
                        text = userProfile.introduction,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = Notosans.displayMedium,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = "${userProfile?.follower ?: 0} followers ・ ${userProfile?.recipeCount ?: 0} recipes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = Notosans.bodySmall,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            onFollowListClick()
                        },
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            UrlImage(
                imageUrl = userProfile?.image ?: "",
                contentDescription = "user profile",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOtherProfileHeader() {
    val userProfile = UserProfile(
        id = 1,
        email = "",
        username = "Username",
        nickname = "Nickname",
        image = "https://source.unsplash.com/random/200x200",
        region = "Seoul",
        introduction = "I wish to be the greatest chef in the world!",
        follower = 1323,
        following = 100,
        recipeCount = 100,
        isFollow = true,
    )

    PengCookTheme {
        OtherProfileHeader(
            userProfile = userProfile,
            onFollowListClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOtherProfileHeaderNoIntroduction() {
    val userProfile = UserProfile(
        id = 1,
        email = "",
        username = "Username",
        nickname = "Nickname",
        image = "https://source.unsplash.com/random/200x200",
        region = "Seoul",
        introduction = "",
        follower = 1323,
        following = 100,
        recipeCount = 100,
        isFollow = true,
    )

    PengCookTheme {
        OtherProfileHeader(
            userProfile = userProfile,
            onFollowListClick = {},
        )
    }
}

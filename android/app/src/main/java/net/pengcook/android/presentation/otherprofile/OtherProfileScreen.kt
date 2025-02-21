@file:OptIn(ExperimentalMaterial3Api::class)

package net.pengcook.android.presentation.otherprofile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.core.model.User
import net.pengcook.android.presentation.otherprofile.components.ButtonRow
import net.pengcook.android.presentation.otherprofile.components.ImageGrid
import net.pengcook.android.presentation.otherprofile.components.OtherProfileHeader
import net.pengcook.android.ui.theme.Notosans
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun OtherProfileScreen(
    state: OtherProfileState,
    onAction: (OtherProfileAction) -> Unit,
) {
    val username = state.userProfile?.username ?: "Username"
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = username,
                        style = Notosans.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { onAction(OtherProfileAction.OnBackClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
        ) {
            OtherProfileHeader(state.userProfile)
            ButtonRow(
                isFollowing = state.isFollowing,
                onFollowClick = {
                    if (state.isFollowing) {
                        onAction(OtherProfileAction.OnUnfollowClick)
                    } else {
                        onAction(OtherProfileAction.OnFollowClick)
                    }
                },
                onBlockClick = { onAction(OtherProfileAction.OnBlockClick) },
                modifier = Modifier.padding(top = 12.dp),
            )
            ImageGrid(state.recipes)
        }
    }
}

@Preview
@Composable
private fun OtherProfileScreenPreview() {
    val recipes = List(12) {
        RecipeForList(
            recipeId = it.toLong(),
            title = "Recipe $it",
            thumbnail = "https://source.unsplash.com/random/200x200",
            user = User(
                id = it.toLong(),
                username = "User $it",
                profile = "https://source.unsplash.com/random/200x200",
            ),
            likeCount = 100,
            commentCount = 10,
            mine = true,
            createdAt = "2021-09-01",
        )
    }

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
        OtherProfileScreen(
            state = OtherProfileState(
                isLoading = false,
                userProfile = userProfile,
                isFollowing = true,
                recipes = recipes,
            ),
            onAction = {},
        )
    }
}

package net.pengcook.android.presentation.follow2.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.presentation.follow2.FollowListAction

@Composable
fun SelectTabRow(
    selectedTabIndex: Int,
    onTabSelect: (FollowListAction) -> Unit,
    followerCount: Long,
    followingCount: Long,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier =
            modifier
                .widthIn(max = 700.dp)
                .fillMaxWidth(),
        containerColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                color = Color.Black,
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            )
        },
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = {
                onTabSelect(FollowListAction.OnTabSelected(0))
            },
            selectedContentColor = Color.Black,
            unselectedContentColor = Color.Black.copy(alpha = 0.5f),
        ) {
            Text(
                text = "$followerCount Followers",
                modifier =
                    Modifier
                        .padding(vertical = 12.dp),
            )
        }
        Tab(
            selected = selectedTabIndex == 1,
            onClick = {
                onTabSelect(FollowListAction.OnTabSelected(1))
            },
            selectedContentColor = Color.Black,
            unselectedContentColor = Color.Black.copy(alpha = 0.5f),
        ) {
            Text(
                text = "$followingCount Following",
                modifier =
                    Modifier
                        .padding(vertical = 12.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTabRow() {
    SelectTabRow(
        selectedTabIndex = 0,
        onTabSelect = {},
        followerCount = 0L,
        followingCount = 0L,
    )
}

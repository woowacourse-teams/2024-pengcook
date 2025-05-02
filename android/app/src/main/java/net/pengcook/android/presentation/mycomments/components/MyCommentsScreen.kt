package net.pengcook.android.presentation.mycomments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.pengcook.android.presentation.core.components.TopBarWithNavigation
import net.pengcook.android.presentation.core.model.MyComment
import net.pengcook.android.ui.theme.PengCookTheme

@Composable
fun MyCommentsScreen(
    comments: List<MyComment>,
    navigateToDetail: (Long) -> Unit,
    navigationBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier
            .background(Color.White)
            .fillMaxWidth(),
        topBar = {
            TopBarWithNavigation(
                name = "내 댓글 목록",
                navigation = navigationBack,
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier
                        .background(Color.White)
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                items(comments) { comment ->
                    MyCommentItem(
                        comment = comment,
                        onCommentClick = { navigateToDetail(comment.recipeId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                    )
                }
            }
        },
    )
}

@Preview
@Composable
private fun MyCommentsScreenPreview() {
    PengCookTheme {
        MyCommentsScreen(
            comments =
                listOf(
                    MyComment(
                        commentId = 1,
                        recipeId = 101,
                        recipeTitle = "Delicious Spaghetti",
                        recipeImage = "https://source.unsplash.com/random/200x200",
                        createdAt = "2024-12-15",
                        message = "This recipe is amazing! I loved it. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    ),
                    MyComment(
                        commentId = 1,
                        recipeId = 101,
                        recipeTitle = "Delicious Spaghetti",
                        recipeImage = "https://source.unsplash.com/random/200x200",
                        createdAt = "2024-12-15",
                        message = "This recipe is amazing! I loved it. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    ),
                ),
            navigationBack = {},
            navigateToDetail = {},
        )
    }
}

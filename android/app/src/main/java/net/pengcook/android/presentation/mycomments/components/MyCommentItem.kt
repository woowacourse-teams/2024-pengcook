package net.pengcook.android.presentation.mycomments.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.pengcook.android.presentation.core.components.UrlImage
import net.pengcook.android.presentation.core.model.MyComment

@Composable
fun MyCommentItem(
    comment: MyComment,
    onCommentClick: (Long) -> Unit,
    modifier: Modifier,
) {
    val imageWidth: Dp = 64.dp
    val imageHeight: Dp = 64.dp

    Card(
        modifier = modifier.padding(start = 16.dp, end = 8.dp),
        colors = CardDefaults.cardColors(Color.White),
        onClick = { onCommentClick(comment.recipeId) },
    ) {
        Row(modifier = Modifier) {
            MyCommentItemDescription(
                comment = comment,
                modifier =
                    Modifier
                        .weight(1f),
            )
            UrlImage(
                imageUrl = comment.recipeImage,
                contentDescription = "",
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .width(imageWidth)
                        .height(imageHeight),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyComment() {
    val tempMessage = "This recipe is amazing! I loved it. Lorem ipsum dolor sit amet, consectetur adipiscing elit."

    val sampleComment =
        MyComment(
            commentId = 1,
            recipeId = 101,
            recipeTitle = "Delicious Spaghetti",
            createdAt = "2024-12-15",
            recipeImage = "https://source.unsplash.com/random/200x200",
            message = tempMessage,
        )
    MyCommentItem(
        comment = sampleComment,
        onCommentClick = {},
        modifier = Modifier.fillMaxWidth(),
    )
}

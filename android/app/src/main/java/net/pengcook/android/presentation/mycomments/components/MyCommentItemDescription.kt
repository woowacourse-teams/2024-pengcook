package net.pengcook.android.presentation.mycomments.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.pengcook.android.presentation.core.model.MyComment
import nextstep.shoppingcart.ui.theme.Notosans

@Composable
fun MyCommentItemDescription(
    comment: MyComment,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = comment.recipeTitle,
            style = Notosans.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = comment.message,
            style = Notosans.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyCommentItemDescription() {
    val sampleComment =
        MyComment(
            commentId = 1,
            recipeId = 101,
            recipeTitle = "Delicious Spaghetti",
            createdAt = "2024-12-15",
            recipeImage = "https://source.unsplash.com/random/200x200",
            message = "This recipe is amazing! I loved it. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla  facilisi. Donec nec purus nec nunc  consectetur adipiscing elit. Nulla  facilisi. Donec nec purus nec nunc",
        )
    MyCommentItemDescription(comment = sampleComment)
}

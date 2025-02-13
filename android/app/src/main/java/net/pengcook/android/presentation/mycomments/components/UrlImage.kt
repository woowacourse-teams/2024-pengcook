package net.pengcook.android.presentation.mycomments.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage

@Composable
fun UrlImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewUrlImage() {
    UrlImage(
        imageUrl = "https://source.unsplash.com/random/200x200",
        contentDescription = "",
    )
}

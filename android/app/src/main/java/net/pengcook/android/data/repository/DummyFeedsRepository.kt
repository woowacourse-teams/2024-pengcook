package net.pengcook.android.data.repository

import net.pengcook.android.model.Feed

class DummyFeedsRepository {
    suspend fun fetchFeeds(pageNumber: Int, size: Int): List<Feed> {
        return (0 until size).map {
            Feed(
                id = it.toLong(),
                username = "username$it",
                profileImageUrl = "https://cdn.crowdpic.net/detail-thumb/thumb_d_23832961B1BC3712116E240165A4FAF6.jpg",
                recipeImageUrl = "https://cdn.crowdpic.net/detail-thumb/thumb_d_23832961B1BC3712116E240165A4FAF6.jpg",
                recipeTitle = "Recipe Title $it",
                likeCount = it,
                commentCount = it
            )
        }
    }
}

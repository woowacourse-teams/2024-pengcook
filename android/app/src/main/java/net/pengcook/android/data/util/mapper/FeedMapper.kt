package net.pengcook.android.data.util.mapper

import net.pengcook.android.data.model.feed.item.FeedItemResponse
import net.pengcook.android.data.model.feed.step.RecipeStepResponse
import net.pengcook.android.presentation.core.model.Feed
import net.pengcook.android.presentation.detail.RecipeStep

fun FeedItemResponse.toFeed(): Feed =
    Feed(
        id = recipeId,
        username = author.authorName,
        profileImageUrl = author.authorImage,
        recipeImageUrl = thumbnail,
        recipeTitle = title,
        likeCount = likeCount,
        commentCount = 0
    )

fun RecipeStepResponse.toRecipeStep(): RecipeStep =
    RecipeStep(
        stepId = id,
        recipeId = recipeId,
        description = description,
        image = image,
        sequence = sequence
    )

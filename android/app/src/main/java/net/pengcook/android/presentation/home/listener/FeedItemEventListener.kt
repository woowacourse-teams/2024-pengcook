package net.pengcook.android.presentation.home.listener

import net.pengcook.android.presentation.core.model.Recipe

interface FeedItemEventListener {
    fun onNavigateToDetail(recipe: Recipe)
}

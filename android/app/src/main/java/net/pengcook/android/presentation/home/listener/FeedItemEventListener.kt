package net.pengcook.android.presentation.home.listener

import net.pengcook.android.presentation.core.model.Feed

interface FeedItemEventListener {
    fun onNavigateToDetail(feedInfo: Feed)
}

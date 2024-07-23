package net.pengcook.android.listner

import net.pengcook.android.model.Feed

interface FeedItemEventListener {
    fun onNavigateToDetail(feedInfo: Feed)
}

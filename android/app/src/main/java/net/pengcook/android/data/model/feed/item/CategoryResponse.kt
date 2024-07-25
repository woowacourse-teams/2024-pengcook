package net.pengcook.android.data.model.feed.item

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("categoryId")
    val categoryId: Long,
    @SerializedName("categoryName")
    val categoryName: String,
)

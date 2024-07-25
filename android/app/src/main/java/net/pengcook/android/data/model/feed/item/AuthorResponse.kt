package net.pengcook.android.data.model.feed.item


import com.google.gson.annotations.SerializedName

data class AuthorResponse(
    @SerializedName("authorId")
    val authorId: Long,
    @SerializedName("authorImage")
    val authorImage: String,
    @SerializedName("authorName")
    val authorName: String
)

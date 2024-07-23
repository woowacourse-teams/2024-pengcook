package net.pengcook.android.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val title: String,
    val category: String,
    val thumbnail: String,
    val user: User,
    val favoriteCount: Long,
    val ingredients: List<String>,
    val timeRequired: Int,
    val difficulty: String,
    val introduction: String,
) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(thumbnail)
        parcel.writeParcelable(user, flags)
        parcel.writeLong(favoriteCount)
        parcel.writeStringList(ingredients)
        parcel.writeInt(timeRequired)
        parcel.writeString(difficulty)
        parcel.writeString(introduction)
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe =
            Recipe(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readParcelable(User::class.java.classLoader) ?: User("", ""),
                parcel.readLong(),
                parcel.createStringArrayList() ?: listOf(),
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
            )

        override fun newArray(size: Int): Array<Recipe?> = arrayOfNulls(size)
    }
}

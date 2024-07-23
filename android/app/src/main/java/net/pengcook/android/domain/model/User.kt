package net.pengcook.android.domain.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    val username: String,
    val profile: String,
) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int,
    ) {
        parcel.writeString(username)
        parcel.writeString(profile)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User =
            User(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
            )

        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}

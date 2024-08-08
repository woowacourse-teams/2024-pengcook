package net.pengcook.android.data.repository.profile

import net.pengcook.android.data.model.profile.UpdateProfileRequest
import net.pengcook.android.domain.model.profile.UserProfile
import net.pengcook.android.presentation.core.model.Recipe

interface ProfileRepository {
    suspend fun fetchUserInformation(userId: Long): Result<UserProfile>

    suspend fun fetchMyUserInformation(): Result<UserProfile>

    suspend fun patchMyUserInformation(userProfile: UpdateProfileRequest): Result<Unit>

    suspend fun fetchUserFeeds(
        userId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Result<List<Recipe>>
}

package net.pengcook.android.presentation.signup

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.pengcook.android.data.datasource.auth.FakeSessionLocalDataSource
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.FakeAuthorizationRepository
import net.pengcook.android.data.repository.auth.FakeSessionRepository
import net.pengcook.android.data.repository.auth.SessionRepository
import net.pengcook.android.data.repository.image.FakeImageRepository
import net.pengcook.android.data.repository.photo.ImageRepository
import net.pengcook.android.presentation.util.getOrAwaitValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {
    private lateinit var viewModel: SignUpViewModel
    private lateinit var sessionRepository: SessionRepository
    private lateinit var authorizationRepository: AuthorizationRepository
    private val imageRepository: ImageRepository = FakeImageRepository()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionRepository = FakeSessionRepository(FakeSessionLocalDataSource())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun changeProfileImage_success_uriChanged() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(
                    registered = false,
                    usernames = emptyList(),
                    fakeTokenLocalDataSource = FakeSessionLocalDataSource(),
                )

            viewModel =
                SignUpViewModel(
                    platformName = "google",
                    authorizationRepository = authorizationRepository,
                    sessionRepository = sessionRepository,
                    imageRepository = imageRepository,
                )

            // when
            viewModel.changeCurrentImage(Uri.parse("new_uri"))
            advanceUntilIdle()

            // then
            val actual = viewModel.imageUri.getOrAwaitValue()
            assertEquals("new_uri", actual.toString())
        }

    @Test
    fun onSelectionChange_success_countryChanged() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(
                    registered = false,
                    usernames = emptyList(),
                    fakeTokenLocalDataSource = FakeSessionLocalDataSource(),
                )

            viewModel =
                SignUpViewModel(
                    platformName = "google",
                    authorizationRepository = authorizationRepository,
                    sessionRepository = sessionRepository,
                    imageRepository = imageRepository,
                )

            // when
            viewModel.onSelectionChange("Korea")
            advanceUntilIdle()

            // then
            val actual = viewModel.country.getOrAwaitValue()
            assertEquals("Korea", actual)
        }
}

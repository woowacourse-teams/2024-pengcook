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
import net.pengcook.android.data.datasource.auth.FakeTokenLocalDataSource
import net.pengcook.android.data.repository.auth.AuthorizationRepository
import net.pengcook.android.data.repository.auth.FakeAuthorizationRepository
import net.pengcook.android.data.repository.auth.FakeTokenRepository
import net.pengcook.android.data.repository.auth.TokenRepository
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
    private lateinit var tokenRepository: TokenRepository
    private lateinit var authorizationRepository: AuthorizationRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        tokenRepository = FakeTokenRepository(FakeTokenLocalDataSource())
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
                    fakeTokenLocalDataSource = FakeTokenLocalDataSource(),
                )

            viewModel =
                SignUpViewModel(
                    platformName = "google",
                    authorizationRepository = authorizationRepository,
                    tokenRepository = tokenRepository,
                )

            // when
            viewModel.changeProfileImage(Uri.parse("new_uri"))
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
                    fakeTokenLocalDataSource = FakeTokenLocalDataSource(),
                )

            viewModel =
                SignUpViewModel(
                    platformName = "google",
                    authorizationRepository = authorizationRepository,
                    tokenRepository = tokenRepository,
                )

            // when
            viewModel.onSelectionChange("Korea")
            advanceUntilIdle()

            // then
            val actual = viewModel.country.getOrAwaitValue()
            assertEquals("Korea", actual)
        }
}

package net.pengcook.android.presentation.onboarding

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

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private lateinit var authorizationRepository: AuthorizationRepository
    private lateinit var tokenRepository: TokenRepository
    private lateinit var viewModel: OnboardingViewModel

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
    fun signIn_success_navigatesToMain() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(true, emptyList(), FakeTokenLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, tokenRepository)

            // when
            viewModel.signIn("google", "googleToken")
            advanceUntilIdle()

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(OnboardingUiEvent.NavigateToMain, actual)
        }

    @Test
    fun signIn_userDoesNotExist_navigatesToSignUp() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(false, emptyList(), FakeTokenLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, tokenRepository)

            // when
            viewModel.signIn("google", "googleToken")
            advanceUntilIdle()

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(OnboardingUiEvent.NavigateToSignUp("google"), actual)
        }

    @Test
    fun signIn_failureDueToInvalidPlatformToken_notifiesError() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(false, emptyList(), FakeTokenLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, tokenRepository)

            // when
            viewModel.signIn("google", "invalidGoogleToken")
            advanceUntilIdle()

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(OnboardingUiEvent.Error, actual)
        }

    @Test
    fun signIn_failureDueToInvalidPlatformName_notifiesError() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(false, emptyList(), FakeTokenLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, tokenRepository)

            // when
            viewModel.signIn("gogle", "googleToken")
            advanceUntilIdle()

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(OnboardingUiEvent.Error, actual)
        }
}

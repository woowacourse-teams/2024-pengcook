package net.pengcook.android.presentation.onboarding

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
import net.pengcook.android.presentation.util.getOrAwaitValue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private lateinit var authorizationRepository: AuthorizationRepository
    private lateinit var sessionRepository: SessionRepository
    private lateinit var viewModel: OnboardingViewModel

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
    fun signIn_success_navigatesToMain() =
        runTest {
            // given
            authorizationRepository =
                FakeAuthorizationRepository(true, emptyList(), FakeSessionLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, sessionRepository)

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
                FakeAuthorizationRepository(false, emptyList(), FakeSessionLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, sessionRepository)

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
                FakeAuthorizationRepository(false, emptyList(), FakeSessionLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, sessionRepository)

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
                FakeAuthorizationRepository(false, emptyList(), FakeSessionLocalDataSource())
            viewModel = OnboardingViewModel(authorizationRepository, sessionRepository)

            // when
            viewModel.signIn("gogle", "googleToken")
            advanceUntilIdle()

            // then
            val actual = viewModel.uiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertEquals(OnboardingUiEvent.Error, actual)
        }
}

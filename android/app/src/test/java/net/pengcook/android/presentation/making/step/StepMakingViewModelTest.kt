package net.pengcook.android.presentation.making.step

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.pengcook.android.data.datasource.making.FakeRecipeStepMakingDatasource
import net.pengcook.android.data.repository.making.FakeRecipeStepMakingRepository
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class StepMakingViewModelTest {
    private lateinit var stepMakingRepository: RecipeStepMakingRepository
    private lateinit var viewModel: StepMakingViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        stepMakingRepository = FakeRecipeStepMakingRepository(FakeRecipeStepMakingDatasource())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `step is "1" when initialized`() {
        // given
        viewModel = StepMakingViewModel(1, 15, stepMakingRepository)

        // when
        val actual = viewModel.stepNumber.value

        // then
        assertEquals(1, actual)
    }
}

  /*  @Test
    fun `fetch recipe step data when it exist already`() {
        runTest {
            // given & when
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()

            // then
            val actual = viewModel.introductionContent.getOrAwaitValue()
            assertEquals("description1", actual)
        }
    }

    @Test
    fun `step increases when moved to next page`() {
        runTest {
            // given
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.validateNextPageableCondition()

            val actual = viewModel.stepNumber.value

            // then
            assertEquals(2, actual)
        }
    }

    @Test
    fun `uploadStep success`() {
      // given
             viewModel = StepMakingViewModel(1, 15, stepMakingRepository)

             // when
             viewModel.uploadStep(1, "description", "image")

             // then
             val actual = viewModel.completeStepMakingState.value?.getContentIfNotHandled()
             assertEquals(true, actual)

    }
   */

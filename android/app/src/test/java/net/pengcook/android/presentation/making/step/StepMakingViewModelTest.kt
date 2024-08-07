package net.pengcook.android.presentation.making.step

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.pengcook.android.data.datasource.making.FakeRecipeStepMakingDatasource
import net.pengcook.android.data.repository.making.FakeRecipeStepMakingRepository
import net.pengcook.android.data.repository.making.step.RecipeStepMakingRepository
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
    fun `1 maxStepPage initialized by maximumStep`() {
        // maxStepPage 초기화 테스트
        // given
        viewModel = StepMakingViewModel(1, 15, stepMakingRepository)

        // when
        val actual = viewModel.maxStepPage.getOrAwaitValue()

        // then
        assertEquals(15, actual)
    }

    @Test
    fun `2 step is "1" when initialized`() {
        // step이 1로 초기화 되는지 테스트
        // given
        viewModel = StepMakingViewModel(1, 15, stepMakingRepository)

        // when
        val actual = viewModel.stepNumber.getOrAwaitValue()

        // then
        assertEquals(1, actual)
    }

    @Test
    fun `3 fetch recipe step data when it exist already`() {
        // 이미 존재하는 레시피 스텝 데이터를 가져오는지 테스트
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
    fun `4 step increases when moved to next page`() {
        // 다음 페이지로 이동했을 때 step이 증가하는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.validateNextPageableCondition()

            val actual = viewModel.stepNumber.getOrAwaitValue()

            // then
            assertEquals(2, actual)
        }
    }

    @Test
    fun `5 can not fetch recipe step data when it is not exist`() {
        // 레시피 스텝 데이터가 존재하지 않을 때 데이터를 가져오지 않는지 테스트
        runTest {
            // given & when
            viewModel = StepMakingViewModel(2, 15, stepMakingRepository)
            advanceUntilIdle()

            // then
            val actual = viewModel.introductionContent.getOrAwaitValue()
            assertEquals("", actual)
        }
    }

    @Test
    fun `6 step does not increases when introduction is empty`() {
        // introduction 이 비어있을 때 step이 증가하지 않는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(2, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            assertEquals(1, viewModel.stepNumber.getOrAwaitValue())
            viewModel.validateNextPageableCondition()

            val actual = viewModel.stepNumber.getOrAwaitValue()

            // then
            assertEquals(1, actual)
        }
    }

    @Test
    fun `7 step does not increases when step is equal with maximumStep`() {
        // step이 maximumStep과 같을 때 step이 증가하지 않는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(15, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            for (i in 1..15) {
                viewModel.introductionContent.value = "description$i"
                viewModel.validateNextPageableCondition()
            }

            val actual = viewModel.stepNumber.getOrAwaitValue()

            // then
            assertEquals(15, actual)
        }
    }

    @Test
    fun `8 step does not decrease when step is equal with 1`() {
        // step이 1일 때 step이 감소하지 않는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.validatePreviousPageableCondition()

            val actual = viewModel.stepNumber.getOrAwaitValue()

            // then
            assertEquals(1, actual)
        }
    }

    @Test
    fun `9 step decreases when moved to previous page`() {
        // 이전 페이지로 이동했을 때 step이 감소하는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(2, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.validatePreviousPageableCondition()

            val actual = viewModel.stepNumber.getOrAwaitValue()

            // then
            assertEquals(1, actual)
        }
    }

    @Test
    fun `10 quit event called when navigationAction is called`() {
        // navigationAction이 호출되었을 때 quit event가 호출되는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.navigationAction()

            val actual = viewModel.quitStepMakingState.getOrAwaitValue()

            // then
            assertEquals(true, actual.getContentIfNotHandled())
        }
    }

    @Test
    fun `11 emptyIntroductionState called when introductionContent is empty`() {
        // introduction이 비어있을 때 emptyIntroductionState가 호출되는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(2, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.customAction()
            val actual = viewModel.emptyIntroductionState.getOrAwaitValue()

            // then
            assertEquals(true, actual.getContentIfNotHandled())
        }
    }

    @Test
    fun `12 datasource data changed when page moved next`() {
        // 다음 페이지로 이동했을 때 데이터 소스의 데이터가 변경되는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()

            // when
            viewModel.introductionContent.value = "new description"
            advanceUntilIdle()
            assertEquals("new description", viewModel.introductionContent.getOrAwaitValue())
            viewModel.validateNextPageableCondition()
            advanceUntilIdle()

            assertEquals(2, viewModel.stepNumber.getOrAwaitValue())
            assertEquals("description2", viewModel.introductionContent.getOrAwaitValue())

            viewModel.validatePreviousPageableCondition()
            advanceUntilIdle()

            assertEquals(viewModel.stepNumber.getOrAwaitValue(), 1)
            assertEquals("new description", viewModel.introductionContent.getOrAwaitValue())
        }
    }

    @Test
    fun `13 can not complete making step when introduction is empty`() {
        // introduction이 비어있을 때 스텝 제작을 완료할 수 없는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(2, 15, stepMakingRepository)
            advanceUntilIdle()
            assertEquals("", viewModel.introductionContent.getOrAwaitValue())

            // when
            viewModel.customAction()
            advanceUntilIdle()
            val actual = viewModel.emptyIntroductionState.getOrAwaitValue()

            // then
            assertEquals(true, actual.getContentIfNotHandled())
        }
    }

    @Test
    fun `14 datasource data changes when page moved previous`() {
        // 이전 페이지로 이동했을 때 데이터 소스의 데이터가 변경되는지 테스트
        runTest {
            // given
            viewModel = StepMakingViewModel(1, 15, stepMakingRepository)
            advanceUntilIdle()
            viewModel.validateNextPageableCondition()
            advanceUntilIdle()
            assertEquals(2, viewModel.stepNumber.getOrAwaitValue())

            // when
            viewModel.introductionContent.value = "new description"
            advanceUntilIdle()
            assertEquals("new description", viewModel.introductionContent.getOrAwaitValue())
            viewModel.validatePreviousPageableCondition()
            advanceUntilIdle()

            assertEquals(1, viewModel.stepNumber.getOrAwaitValue())
            assertEquals("description1", viewModel.introductionContent.getOrAwaitValue())

            // then
            viewModel.validateNextPageableCondition()
            advanceUntilIdle()

            assertEquals(2, viewModel.stepNumber.getOrAwaitValue())
            assertEquals("new description", viewModel.introductionContent.getOrAwaitValue())
        }
    }

    // TODO `step does not increases when uploading image`()
    // TODO `14 can not complete making step when step is not equal with maximumStep`() {
    // step이 maximumStep과 같지 않을 때 스텝 제작을 완료할 수 없는지 테스트
}

package net.pengcook.android.presentation.recipemaking

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.pengcook.android.data.repository.makingrecipe.DefaultMakingRecipeRepository
import net.pengcook.android.presentation.core.util.Event
import net.pengcook.android.presentation.making.MakingEvent
import net.pengcook.android.presentation.making.RecipeMakingViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.File

@ExperimentalCoroutinesApi
class RecipeMakingViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val repository: DefaultMakingRecipeRepository = mockk()

    private val eventObserver: Observer<Event<MakingEvent>> = mockk(relaxed = true)
    private val eventSlot = slot<Event<MakingEvent>>()

    private lateinit var viewModel: RecipeMakingViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RecipeMakingViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchImageUri success`() =
        runTest {
            val keyName = "test-key"
            val url = "http://example.com/image.jpg"

            coEvery { repository.fetchImageUri(keyName) } returns url

            viewModel.uiEvent.observeForever(eventObserver)
            viewModel.fetchImageUri(keyName)

            testDispatcher.scheduler.advanceUntilIdle()

            verify { eventObserver.onChanged(capture(eventSlot)) }
            coVerify { repository.fetchImageUri(keyName) }
            assertEquals(
                eventSlot.captured.getContentIfNotHandled(),
                MakingEvent.PresignedUrlRequestSuccessful(url),
            )
        }

    @Test
    fun `fetchImageUri failure`() =
        runTest {
            val keyName = "test-key"
            val errorMessage = "Pre-signed URL 요청 실패"

            coEvery { repository.fetchImageUri(keyName) } throws RuntimeException(errorMessage)

            viewModel.uiEvent.observeForever(eventObserver)

            viewModel.fetchImageUri(keyName)

            testDispatcher.scheduler.advanceUntilIdle()

            verify { eventObserver.onChanged(capture(eventSlot)) }
            coVerify { repository.fetchImageUri(keyName) }
            assertEquals(
                eventSlot.captured.getContentIfNotHandled(),
                MakingEvent.PostImageFailure,
            )
        }

    @Test
    fun `uploadImageToS3 success`() =
        runTest {
            val presignedUrl = "http://example.com/upload"
            val file = File("path/to/file")

            coJustRun { repository.uploadImageToS3(presignedUrl, file) }

            viewModel.uiEvent.observeForever(eventObserver)

            viewModel.uploadImageToS3(presignedUrl, file)

            testDispatcher.scheduler.advanceUntilIdle()

            verify { eventObserver.onChanged(capture(eventSlot)) }
            coVerify { repository.uploadImageToS3(presignedUrl, file) }
            assertEquals(
                eventSlot.captured.getContentIfNotHandled(),
                MakingEvent.PostImageSuccessful,
            )
        }

    @Test
    fun `uploadImageToS3 failure`() =
        runTest {
            val presignedUrl = "http://example.com/upload"
            val file = File("path/to/file")
            val errorMessage = "이미지 업로드 실패"

            coEvery { repository.uploadImageToS3(presignedUrl, file) } throws
                RuntimeException(
                    errorMessage,
                )

            viewModel.uiEvent.observeForever(eventObserver)

            viewModel.uploadImageToS3(presignedUrl, file)

            testDispatcher.scheduler.advanceUntilIdle()

            verify { eventObserver.onChanged(capture(eventSlot)) }
            coVerify { repository.uploadImageToS3(presignedUrl, file) }
            assertEquals(eventSlot.captured.getContentIfNotHandled(), MakingEvent.PostImageFailure)
        }
}

package com.misw.vinilos.ui.collectors

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.CollectorRepository
import com.misw.vinilos.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CollectorViewModelTestExtended {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchCollectors_emitsEmptyListWhenNoCollectors() = runTest {
        val expected = emptyList<Collector>()
        val viewModel = CollectorViewModel(
            CollectorRepository(FakeVinilosApiService(result = expected))
        )
        viewModel.fetchCollectors()
        advanceUntilIdle()
        assertEquals(0, viewModel.collectors.value?.size)
        assertNull(viewModel.error.value)
    }

    @Test
    fun fetchCollectors_initialState_loadingIsFalseAfterCompletion() = runTest {
        val expected = listOf(Collector(1, "Ana", "111", "ana@ana.com"))
        val viewModel = CollectorViewModel(
            CollectorRepository(FakeVinilosApiService(result = expected))
        )
        viewModel.fetchCollectors()
        advanceUntilIdle()
        assertEquals(false, viewModel.isLoading.value)
    }

    private class FakeVinilosApiService(
        private val result: List<Collector> = emptyList(),
        private val error: Throwable? = null
    ) : VinilosApiService {
        override suspend fun getCollectors(): List<Collector> {
            error?.let { throw it }
            return result
        }
        override suspend fun getAlbums() = emptyList<com.misw.vinilos.data.models.Album>()
        override suspend fun getAlbum(id: Int) = throw NotImplementedError()
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
    }
}


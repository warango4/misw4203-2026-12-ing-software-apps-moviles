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
class CollectorViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchCollectors_success_updatesLiveData() = runTest {
        val expected = listOf(Collector(1, "Manolo Bellon", "3502457896", "manollo@caracol.com.co"))
        val viewModel = CollectorViewModel(
            CollectorRepository(FakeVinilosApiService(result = expected))
        )
        viewModel.fetchCollectors()
        advanceUntilIdle()
        assertEquals(expected, viewModel.collectors.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun fetchCollectors_error_updatesErrorLiveData() = runTest {
        val viewModel = CollectorViewModel(
            CollectorRepository(FakeVinilosApiService(error = IllegalStateException("sin conexion")))
        )
        viewModel.fetchCollectors()
        advanceUntilIdle()
        assertEquals("sin conexion", viewModel.error.value)
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


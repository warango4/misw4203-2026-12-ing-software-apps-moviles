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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CollectorViewModelInitializationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetchCollectors_initialLoadingState() = runTest {
        val viewModel = CollectorViewModel(
            CollectorRepository(FakeVinilosApiService())
        )
        advanceUntilIdle()
        assertEquals(false, viewModel.isLoading.value)
    }

    private class FakeVinilosApiService() : VinilosApiService {
        override suspend fun getCollectors(): List<Collector> = emptyList()
        override suspend fun getAlbums() = emptyList<com.misw.vinilos.data.models.Album>()
        override suspend fun getAlbum(id: Int) = throw NotImplementedError()
        override suspend fun getMusicians() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getBands() = emptyList<com.misw.vinilos.data.models.Performer>()
        override suspend fun getMusician(id: Int) = throw NotImplementedError()
        override suspend fun getBand(id: Int) = throw NotImplementedError()
    }
}


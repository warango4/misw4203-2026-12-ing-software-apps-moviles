package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.dispatchers.DefaultDispatcherProvider
import com.misw.vinilos.data.dispatchers.DispatcherProvider
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.withContext

class CollectorRepository(
    private val api: VinilosApiService,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) {
    suspend fun getCollectors(): List<Collector> {
        try {
            Log.d("CollectorRepository", "getCollectors: request started")
            val result = withContext(dispatchers.io) { api.getCollectors() }
            Log.d("CollectorRepository", "getCollectors: success count=${result.size}")
            return result
        } catch (e: Exception) {
            Log.e("CollectorRepository", "getCollectors: failure message=${e.message}", e)
            throw e
        }
    }

    suspend fun getCollector(id: Int): Collector {
        try {
            Log.d("CollectorRepository", "getCollector: request started id=$id")
            val result = withContext(dispatchers.io) { api.getCollector(id) }
            Log.d("CollectorRepository", "getCollector: success id=${result.id} name=${result.name}")
            return result
        } catch (e: Exception) {
            Log.e("CollectorRepository", "getCollector: failure id=$id message=${e.message}", e)
            throw e
        }
    }
}


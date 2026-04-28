package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.network.VinilosApiService

class CollectorRepository(private val api: VinilosApiService) {
    suspend fun getCollectors(): List<Collector> {
        try {
            Log.d("CollectorRepository", "getCollectors: request started")
            val result = api.getCollectors()
            Log.d("CollectorRepository", "getCollectors: success count=${result.size}")
            return result
        } catch (e: Exception) {
            Log.e("CollectorRepository", "getCollectors: failure message=${e.message}", e)
            throw e
        }
    }
}


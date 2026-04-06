package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.network.VinilosApiService

class CollectorRepository(private val api: VinilosApiService) {
    suspend fun getCollectors(): List<Collector> {
        return try {
            Log.d("CollectorRepository", "Calling API to get collectors")
            val result = api.getCollectors()
            Log.d("CollectorRepository", "API returned ${result.size} collectors")
            result
        } catch (e: Exception) {
            Log.e("CollectorRepository", "Error fetching collectors: ${e.message}", e)
            throw e
        }
    }
}


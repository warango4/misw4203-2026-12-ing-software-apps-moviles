package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PerformerRepository(private val api: VinilosApiService) {
    suspend fun getPerformers(): List<Performer> = coroutineScope {
        try {
            Log.d("PerformerRepository", "Calling API to get musicians and bands concurrently")
            val musiciansDeferred = async { api.getMusicians() }
            val bandsDeferred = async { api.getBands() }

            val musicians = musiciansDeferred.await()
            val bands = bandsDeferred.await()

            val allPerformers = musicians + bands
            Log.d("PerformerRepository", "API returned ${allPerformers.size} combined performers")

            allPerformers.sortedBy { it.name }
        } catch (e: Exception) {
            Log.e("PerformerRepository", "Error fetching performers: ${e.message}", e)
            throw e
        }
    }

    suspend fun getPerformer(id: Int, isBand: Boolean): Performer {
        try {
            Log.d("PerformerRepository", "Calling API to get performer $id (isBand=$isBand)")
            val response = if (isBand) {
                api.getBand(id)
            } else {
                api.getMusician(id)
            }
            Log.d("PerformerRepository", "API returned performer ${response.name}")
            return response
        } catch (e: Exception) {
            Log.e("PerformerRepository", "Error fetching performer $id: ${e.message}", e)
            throw e
        }
    }
}

package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class PerformerRepository(private val api: VinilosApiService) {
    suspend fun getPerformers(): List<Performer> = coroutineScope {
        try {
            Log.d("PerformerRepository", "getPerformers: request started (musicians + bands)")
            val musiciansDeferred = async { api.getMusicians() }
            val bandsDeferred = async { api.getBands() }

            val musicians = musiciansDeferred.await()
            val bands = bandsDeferred.await()

            val allPerformers = musicians + bands
            Log.d("PerformerRepository", "getPerformers: success count=${allPerformers.size}")

            allPerformers.sortedBy { it.name }
        } catch (e: Exception) {
            Log.e("PerformerRepository", "getPerformers: failure message=${e.message}", e)
            throw e
        }
    }

    suspend fun getPerformer(id: Int, isBand: Boolean): Performer {
        try {
            Log.d("PerformerRepository", "getPerformer: request started performerId=$id isBand=$isBand")
            val response = if (isBand) {
                api.getBand(id)
            } else {
                api.getMusician(id)
            }
            Log.d("PerformerRepository", "getPerformer: success performerId=$id name=${response.name}")
            return response
        } catch (e: Exception) {
            Log.e("PerformerRepository", "getPerformer: failure performerId=$id message=${e.message}", e)
            throw e
        }
    }
}

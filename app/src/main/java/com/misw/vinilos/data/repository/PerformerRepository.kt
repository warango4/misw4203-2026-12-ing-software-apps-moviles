package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.dispatchers.DefaultDispatcherProvider
import com.misw.vinilos.data.dispatchers.DispatcherProvider
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.network.VinilosApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class PerformerRepository(
    private val api: VinilosApiService,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) {
    suspend fun getPerformers(): List<Performer> = coroutineScope {
        try {
            Log.d("PerformerRepository", "getPerformers: request started (musicians + bands)")
            val musiciansDeferred = async(dispatchers.io) { api.getMusicians() }
            val bandsDeferred = async(dispatchers.io) { api.getBands() }

            val musicians = musiciansDeferred.await()
            val bands = bandsDeferred.await()

            val allPerformers = musicians + bands
            Log.d("PerformerRepository", "getPerformers: success count=${allPerformers.size}")

            withContext(dispatchers.default) { allPerformers.sortedBy { it.name } }
        } catch (e: Exception) {
            Log.e("PerformerRepository", "getPerformers: failure message=${e.message}", e)
            throw e
        }
    }

    suspend fun getPerformer(id: Int, isBand: Boolean): Performer {
        try {
            Log.d("PerformerRepository", "getPerformer: request started performerId=$id isBand=$isBand")
            val response = withContext(dispatchers.io) {
                if (isBand) {
                    api.getBand(id)
                } else {
                    api.getMusician(id)
                }
            }
            Log.d("PerformerRepository", "getPerformer: success performerId=$id name=${response.name}")
            return response
        } catch (e: Exception) {
            Log.e("PerformerRepository", "getPerformer: failure performerId=$id message=${e.message}", e)
            throw e
        }
    }
}

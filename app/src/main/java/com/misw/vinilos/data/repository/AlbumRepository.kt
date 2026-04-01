package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService

class AlbumRepository(private val api: VinilosApiService) {
    suspend fun getAlbums(): List<Album> {
        Log.d("AlbumRepository", "Calling API to get albums")
        val result = api.getAlbums()
        Log.d("AlbumRepository", "API returned ${result.size} albums")
        return result
    }
}
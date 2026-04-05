package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService

class AlbumRepository(private val api: VinilosApiService) {
    suspend fun getAlbums(): List<Album> {
        try {
            Log.d("AlbumRepository", "Calling API to get albums")
            val result = api.getAlbums()
            Log.d("AlbumRepository", "API returned ${result.size} albums")
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "Error fetching albums: ${e.message}", e)
            throw e
        }
    }

    suspend fun getAlbum(id: Int): Album {
        try {
            Log.d("AlbumRepository", "Calling API to get album $id")
            val result = api.getAlbum(id)
            Log.d("AlbumRepository", "API returned album ${result.name}")
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "Error fetching album $id: ${e.message}", e)
            throw e
        }
    }
}
package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.AlbumRequest
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.models.TrackRequest
import com.misw.vinilos.data.network.VinilosApiService

class AlbumRepository(
    private val api: VinilosApiService,
    private val onMutationSuccess: () -> Unit = {}
) {
    suspend fun getAlbums(): List<Album> {
        try {
            Log.d("AlbumRepository", "getAlbums: request started")
            val result = api.getAlbums()
            Log.d("AlbumRepository", "getAlbums: success count=${result.size}")
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "getAlbums: failure message=${e.message}", e)
            throw e
        }
    }

    suspend fun getAlbum(id: Int, refresh: Boolean = false): Album {
        try {
            Log.d("AlbumRepository", "getAlbum: request started albumId=$id refresh=$refresh")
            val cacheControl = if (refresh) "no-cache" else null
            val result = api.getAlbum(id, cacheControl)
            Log.d("AlbumRepository", "getAlbum: success albumId=$id name=${result.name}")
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "getAlbum: failure albumId=$id message=${e.message}", e)
            throw e
        }
    }

    suspend fun createAlbum(request: AlbumRequest): Album {
        try {
            Log.d("AlbumRepository", "createAlbum: request started name=${request.name}")
            val result = api.createAlbum(request)
            Log.d("AlbumRepository", "createAlbum: success id=${result.id}")
            onMutationSuccess()
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "createAlbum: failure message=${e.message}", e)
            throw e
        }
    }

    suspend fun addTrack(albumId: Int, request: TrackRequest): Track {
        try {
            Log.d("AlbumRepository", "addTrack: request started albumId=$albumId name=${request.name}")
            val result = api.addTrack(albumId, request)
            Log.d("AlbumRepository", "addTrack: success id=${result.id}")
            onMutationSuccess()
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "addTrack: failure message=${e.message}", e)
            throw e
        }
    }
}
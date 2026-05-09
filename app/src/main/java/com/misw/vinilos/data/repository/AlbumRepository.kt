package com.misw.vinilos.data.repository

import android.util.Log
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.network.VinilosApiService

class AlbumRepository(private val api: VinilosApiService) {
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

    suspend fun getAlbum(id: Int): Album {
        try {
            Log.d("AlbumRepository", "getAlbum: request started albumId=$id")
            val result = api.getAlbum(id)
            Log.d("AlbumRepository", "getAlbum: success albumId=$id name=${result.name}")
            return result
        } catch (e: Exception) {
            Log.e("AlbumRepository", "getAlbum: failure albumId=$id message=${e.message}", e)
            throw e
        }
    }
}
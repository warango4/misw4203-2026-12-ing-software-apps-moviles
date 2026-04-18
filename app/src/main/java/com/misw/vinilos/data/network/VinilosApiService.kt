package com.misw.vinilos.data.network

import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.Performer
import retrofit2.http.GET
import retrofit2.http.Path

interface VinilosApiService {
    @GET("albums")
    suspend fun getAlbums(): List<Album>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") id: Int): Album

    @GET("musicians")
    suspend fun getMusicians(): List<Performer>

    @GET("musicians/{id}")
    suspend fun getMusician(@Path("id") id: Int): Performer

    @GET("bands")
    suspend fun getBands(): List<Performer>

    @GET("bands/{id}")
    suspend fun getBand(@Path("id") id: Int): Performer
}

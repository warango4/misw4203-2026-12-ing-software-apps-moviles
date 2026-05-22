package com.misw.vinilos.data.network

import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.models.AlbumRequest
import com.misw.vinilos.data.models.Collector
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.models.TrackRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VinilosApiService {
    @GET("albums")
    suspend fun getAlbums(): List<Album>

    @GET("albums/{id}")
    suspend fun getAlbum(
        @Path("id") id: Int,
        @retrofit2.http.Header("Cache-Control") cacheControl: String? = null
    ): Album

    @POST("albums")
    suspend fun createAlbum(@Body album: AlbumRequest): Album

    @POST("albums/{albumId}/tracks")
    suspend fun addTrack(@Path("albumId") albumId: Int, @Body track: TrackRequest): Track

    @GET("musicians")
    suspend fun getMusicians(): List<Performer>

    @GET("musicians/{id}")
    suspend fun getMusician(@Path("id") id: Int): Performer

    @GET("bands")
    suspend fun getBands(): List<Performer>

    @GET("bands/{id}")
    suspend fun getBand(@Path("id") id: Int): Performer

    @GET("collectors")
    suspend fun getCollectors(): List<Collector>

    @GET("collectors/{id}")
    suspend fun getCollector(@Path("id") id: Int): Collector
}

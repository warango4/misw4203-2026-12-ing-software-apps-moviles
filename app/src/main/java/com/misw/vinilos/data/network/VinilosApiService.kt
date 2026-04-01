package com.misw.vinilos.data.network

import com.misw.vinilos.data.models.Album

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface VinilosApiService {
    @GET("albums")
    suspend fun getAlbums(): List<Album>

    companion object {
        private const val BASE_URL = "https://back-vynils-heroku.herokuapp.com/"

        fun create(): VinilosApiService =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VinilosApiService::class.java)
    }
}
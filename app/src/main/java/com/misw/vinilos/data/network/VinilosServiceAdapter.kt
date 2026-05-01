package com.misw.vinilos.data.network

import android.content.Context
import com.misw.vinilos.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object VinilosServiceAdapter {

    private const val CACHE_DIR_NAME = "http_cache"
    private const val CACHE_SIZE_BYTES: Long = 10L * 1024L * 1024L // 10MB

    /**
     * Crea el ApiService usando un OkHttpClient con caché.
     *
     * Preferir esta sobrecarga desde el código de UI/ViewModels para habilitar la caché.
     */
    fun createApiService(context: Context): VinilosApiService {
        val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
        val cache = Cache(cacheDir, CACHE_SIZE_BYTES)

        val logging = HttpLoggingInterceptor().apply {
            // En debug, nos ayuda a diagnosticar; en release, deshabilitado.
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(CacheControlInterceptor(maxAgeSeconds = 60))
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VinilosApiService::class.java)
    }

    /**
     * Compatibilidad hacia atrás.
     *
     * Mantiene el API previo para no romper tests/unidades, pero NO habilita la caché
     * (no tenemos acceso seguro a un cacheDir sin Context).
     */
    fun createApiService(): VinilosApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VinilosApiService::class.java)
    }
}


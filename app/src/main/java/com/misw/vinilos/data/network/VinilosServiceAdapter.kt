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

    // Singleton del servicio con caché. Se inicializa una sola vez con el ApplicationContext
    // para evitar múltiples instancias de Cache sobre el mismo directorio de disco.
    @Volatile
    private var cachedService: VinilosApiService? = null

    /**
     * Devuelve (o crea) el ApiService singleton con OkHttpClient y caché en disco.
     *
     * Usa el ApplicationContext internamente para que la instancia sobreviva rotaciones
     * de pantalla sin riesgo de memory leaks ni conflictos de DiskLruCache.
     */
    fun createApiService(context: Context): VinilosApiService {
        return cachedService ?: synchronized(this) {
            cachedService ?: buildService(context.applicationContext).also { cachedService = it }
        }
    }

    private fun buildService(appContext: Context): VinilosApiService {
        val cacheDir = File(appContext.cacheDir, CACHE_DIR_NAME)
        val cache = Cache(cacheDir, CACHE_SIZE_BYTES)

        val logging = HttpLoggingInterceptor().apply {
            // En debug, nos ayuda a diagnosticar; en release, deshabilitado.
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
            else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(CacheControlInterceptor(maxAgeSeconds = 60))
            // Sincroniza Espresso con las llamadas de red (en release es no-op)
            .addInterceptor(EspressoIdlingInterceptor())
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
}


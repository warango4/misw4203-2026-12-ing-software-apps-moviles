package com.misw.vinilos.data.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Micro-optimización: fuerza una caché HTTP corta para requests GET idempotentes.
 *
 * Notas:
 * - Evitamos sobreingeniería (sin Room / sin capas extra) y concentramos la mejora
 *   en la creación del HttpClient.
 * - Si el backend no envía headers cacheables, este interceptor vuelve cacheables
 *   las respuestas de GET para reducir refetch al navegar.
 * - TTL corto para minimizar riesgos de data desactualizada.
 */
class CacheControlInterceptor(
    private val maxAgeSeconds: Int = 60
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Solo aplica a GET exitosos. No tocamos POST/PUT/DELETE.
        if (request.method != "GET" || !response.isSuccessful) {
            return response
        }

        // Si el server ya trae Cache-Control, lo respetamos para no pelear con el backend.
        val hasServerCacheControl = response.header("Cache-Control")?.isNotBlank() == true
        if (hasServerCacheControl) {
            return response
        }

        return response.newBuilder()
            .header("Cache-Control", "public, max-age=$maxAgeSeconds")
            .build()
    }
}




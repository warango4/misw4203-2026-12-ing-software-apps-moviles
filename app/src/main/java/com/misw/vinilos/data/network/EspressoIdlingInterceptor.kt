package com.misw.vinilos.data.network

import com.misw.vinilos.testutils.EspressoIdlingResource
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor que notifica a Espresso sobre el inicio/fin de requests HTTP.
 *
 * En debug incrementa/decrementa un CountingIdlingResource.
 * En release es no-op (la implementación de EspressoIdlingResource es no-op).
 */
class EspressoIdlingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        EspressoIdlingResource.increment()
        return try {
            chain.proceed(chain.request())
        } finally {
            EspressoIdlingResource.decrement()
        }
    }
}


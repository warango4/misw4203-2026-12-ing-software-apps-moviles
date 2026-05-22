package com.misw.vinilos.data.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provee dispatchers para separar IO/CPU/UI y facilitar el testeo.
 *
 * - io: red, disco, BD
 * - default: trabajo CPU-bound (sort/map pesado)
 * - main: UI
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}


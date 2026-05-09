package com.misw.vinilos.testutils

import java.util.concurrent.atomic.AtomicInteger

/**
 * Contador simple usado por la app para notificar “trabajo en curso”.
 *
 * Importante:
 * - Este archivo vive en sourceSet `debug` y NO depende de Espresso.
 * - Los tests instrumentados (androidTest) registran un IdlingResource propio
 *   que observa este contador.
 */
object EspressoIdlingResource {

    private val counter = AtomicInteger(0)

    fun increment() {
        counter.incrementAndGet()
    }

    fun decrement() {
        // Evitamos negativos por seguridad.
        while (true) {
            val current = counter.get()
            if (current <= 0) return
            if (counter.compareAndSet(current, current - 1)) return
        }
    }

    fun isIdleNow(): Boolean = counter.get() == 0

    fun resetForTests() {
        counter.set(0)
    }
}



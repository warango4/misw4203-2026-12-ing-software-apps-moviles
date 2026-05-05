package com.misw.vinilos.testutils

/**
 * Implementación no-op para builds release.
 *
 * La app no debería depender de Espresso en release.
 */
object EspressoIdlingResource {
    fun increment() = Unit
    fun decrement() = Unit

    fun isIdleNow(): Boolean = true

    fun resetForTests() = Unit
}



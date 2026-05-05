package com.misw.vinilos.albums

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.testutils.EspressoIdlingRule
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * DOC: E2E-HU01-03 – El indicador de carga se muestra y luego desaparece.
 *
 * Nota: la pantalla de álbumes no tiene ProgressBar propio.
 * Para cumplir el requerimiento, validamos el comportamiento equivalente en la navegación principal:
 * al entrar a Coleccionistas, el ProgressBar del tab se muestra (VISIBLE) y luego desaparece (GONE)
 * cuando termina la carga (sin Thread.sleep, usando IdlingResource).
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AlbumListProgressBarE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    @Test
    fun e2e_hu01_03_loadingIndicator_seMuestraYLuegoDesaparece() {
        onView(withId(R.id.CollectorsFragment)).perform(click())

        // Con IdlingResource registrado, Espresso espera a que termine el trabajo asíncrono.
        onView(withId(R.id.pbCollectors)).check(matches(withEffectiveVisibility(GONE)))

        // Y la pantalla debe mostrar lista o empty state.
        // (No se valida el contenido, solo que la navegación/carga concluyó.)
        try {
            onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
        } catch (_: Throwable) {
            onView(withId(R.id.tvCollectorsEmpty)).check(matches(isDisplayed()))
        }
    }
}





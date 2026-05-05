package com.misw.vinilos.collectors

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.not
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.ui.collectors.CollectorAdapter
import com.misw.vinilos.testutils.EspressoIdlingRule
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CollectorDetailE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    @Before
    fun esperarCargaInicial() {
        // Sin esperas activas: Espresso sincroniza con red vía IdlingResource.
    }

    @Test
    fun e2e_hu05_01_collectorsList_seVisualizaAlAbrirTab() {
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
        onView(withId(R.id.CollectorsFragment)).perform(click())

        // Debe mostrarse la lista o el empty state; con cualquiera el tab se considera correcto.
        // Si hay lista, rvCollectors será visible.
        // Si no hay datos, tvCollectorsEmpty será visible.
        try {
            onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
        } catch (t: Throwable) {
            onView(withId(R.id.tvCollectorsEmpty)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun e2e_hu05_02_clickCollector_navegaADetalle() {
        onView(withId(R.id.CollectorsFragment)).perform(click())

        // Este test aplica sólo si hay al menos un collector. Si no hay datos (empty state),
        // validamos que el empty state sea visible y no intentamos hacer click.
        try {
            onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
            onView(withId(R.id.rvCollectors)).perform(
                RecyclerViewActions.actionOnItemAtPosition<CollectorAdapter.CollectorViewHolder>(0, click())
            )
            // HU06: el detalle debe mostrar al menos nombre, teléfono y correo.
            onView(withId(R.id.tvCollectorDetailTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.tvCollectorDetailTelephone)).check(matches(isDisplayed()))
            onView(withId(R.id.tvCollectorDetailEmail)).check(matches(isDisplayed()))

            // Validación básica de contenido no-vacío (evita pasar si queda en blanco por render asíncrono)
            onView(withId(R.id.tvCollectorDetailTitle)).check(matches(not(withText(""))))

            // Nueva sección: Favorite performers (puede venir con datos o vacío según backend)
            onView(withId(R.id.tvCollectorFavoritePerformersLabel))
                .perform(scrollTo())
            try {
                onView(withId(R.id.cgCollectorFavoritePerformers)).check(matches(isDisplayed()))
            } catch (t2: Throwable) {
                onView(withId(R.id.tvCollectorFavoritePerformersEmpty)).check(matches(isDisplayed()))
            }

            // Navegar atrás sin depender de botón custom (fue eliminado)
            pressBack()

            // Debe volver al listado
            onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
        } catch (t: Throwable) {
            onView(withId(R.id.tvCollectorsEmpty)).check(matches(isDisplayed()))
            onView(withId(R.id.pbCollectors)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }
}


package com.misw.vinilos.collectors

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.ui.collectors.CollectorAdapter
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

    @Before
    fun esperarCargaInicial() {
        // Tests existentes usan sleep; mantenemos consistencia.
        Thread.sleep(8000)
    }

    @Test
    fun e2e_hu05_01_collectorsList_seVisualizaAlAbrirTab() {
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
        onView(withId(R.id.CollectorsFragment)).perform(click())

        // La pantalla puede mostrar loader y luego lista o empty state dependiendo del backend.
        // Esperamos (de forma simple, consistente con el resto de E2E) a que termine la carga.
        Thread.sleep(6000)

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

        // Esperar carga
        Thread.sleep(6000)

        // Este test aplica sólo si hay al menos un collector. Si no hay datos (empty state),
        // validamos que el empty state sea visible y no intentamos hacer click.
        try {
            onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
            onView(withId(R.id.rvCollectors)).perform(
                RecyclerViewActions.actionOnItemAtPosition<CollectorAdapter.CollectorViewHolder>(0, click())
            )
            onView(withId(R.id.tvCollectorDetailTitle)).check(matches(isDisplayed()))
        } catch (t: Throwable) {
            onView(withId(R.id.tvCollectorsEmpty)).check(matches(isDisplayed()))
            onView(withId(R.id.pbCollectors)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }
}


package com.misw.vinilos.collectors

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.testutils.EspressoIdlingRule
import com.misw.vinilos.ui.collectors.CollectorAdapter
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Pruebas E2E que el documento lista como HU06 (Collector Detail).
 *
 * Estas pruebas dependen de datos reales del backend (E2E) y por eso incluyen validaciones
 * tolerantes a estados vacíos (por ejemplo, comentarios/álbumes pueden venir vacíos).
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CollectorDetailHU06E2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    @Before
    fun navegarAlDetalleDeUnCollector() {
        onView(withId(R.id.CollectorsFragment)).perform(click())

        // Si hay lista, entramos al primer collector. Si no, dejamos que el test falle con un mensaje claro.
        onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
        onView(withId(R.id.rvCollectors)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CollectorAdapter.CollectorViewHolder>(0, click())
        )
    }

    @Test
    fun e2e_hu06_01_elDetalleMuestraAvatarConInicial() {
        onView(withId(R.id.tvCollectorInitial))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun e2e_hu06_02_progressBarQuedaOcultoTrasLaCarga() {
        onView(withId(R.id.pbCollectorDetail))
            .check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun e2e_hu06_03_elDetalleMuestraNombreNoVacio() {
        onView(withId(R.id.tvCollectorDetailTitle))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun e2e_hu06_04_elDetalleMuestraEmailNoVacio() {
        onView(withId(R.id.tvCollectorDetailEmail))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun e2e_hu06_05_elDetalleMuestraTelefonoNoVacio() {
        onView(withId(R.id.tvCollectorDetailTelephone))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun e2e_hu06_06_seccionComentarios_muestraListaOEmptyState() {
        onView(withId(R.id.tvCollectorCommentsLabel)).perform(scrollTo())

        try {
            onView(withId(R.id.rvCollectorComments)).check(matches(isDisplayed()))
        } catch (_: Throwable) {
            onView(withId(R.id.tvCollectorCommentsEmpty)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun e2e_hu06_07_seccionAlbumes_muestraListaOEmptyState() {
        onView(withId(R.id.tvCollectorAlbumsLabel)).perform(scrollTo())

        try {
            onView(withId(R.id.rvCollectorAlbums)).check(matches(isDisplayed()))
        } catch (_: Throwable) {
            onView(withId(R.id.tvCollectorAlbumsEmpty)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun e2e_hu06_08_desdeDetalleCollector_clickEnAlbum_navegaADetalleAlbum() {
        onView(withId(R.id.tvCollectorAlbumsLabel)).perform(scrollTo())

        // Este caso solo aplica si hay albums. Si no hay, el doc dice que puede aparecer empty.
        try {
            onView(withId(R.id.rvCollectorAlbums)).check(matches(isDisplayed()))
            onView(withId(R.id.rvCollectorAlbums)).perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
            onView(withId(R.id.albumName)).check(matches(isDisplayed()))

            pressBack() // volver al detalle del collector
        } catch (_: Throwable) {
            onView(withId(R.id.tvCollectorAlbumsEmpty)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun e2e_hu06_09_pressBack_regresaAlListadoDeCollectors() {
        pressBack()
        onView(withId(R.id.rvCollectors)).check(matches(isDisplayed()))
    }
}



package com.misw.vinilos.albums

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.testutils.EspressoIdlingRule
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AlbumDetailE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    @Before
    fun navegarAlDetalle() {
        onView(withId(R.id.rvAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @Test
    fun e2e_hu02_01_clickEnAlbum_navegaAlDetalle() {
        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu02_02_albumDetalle_muestraNombre() {
        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun e2e_hu02_03_albumDetalle_muestraGeneroSelYFecha() {
        onView(withId(R.id.albumGenre))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumRecordLabel))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        onView(withId(R.id.albumReleaseDate))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu02_04_albumDetalle_muestraTracks() {
        onView(withId(R.id.tracksRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun e2e_hu02_05_albumDetalle_botonAtrasRegresaAlCatalogo() {
        pressBack()
        onView(withId(R.id.rvAlbums))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu02_06_reseleccionarTabAlbumes_desdeDetalle_regresaALista() {
        // Estando en el detalle, re-seleccionar el tab de Álbumes debe volver a la lista.
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }
}

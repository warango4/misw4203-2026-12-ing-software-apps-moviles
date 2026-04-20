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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.utils.EspressoWaits
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

    @Before
    fun navegarAlDetalle() {
        onView(withId(R.id.rvAlbums))
            .perform(EspressoWaits.waitForRecyclerViewItemCount(minItemCount = 1))
        onView(withId(R.id.rvAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
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
        // Evita flakiness por carga asíncrona (en CI puede estar vacío aún)
        onView(withId(R.id.albumName))
            .perform(EspressoWaits.waitForNonEmptyText(timeoutMs = 30_000))
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
        // En NestedScrollView, scrollTo() sobre un RecyclerView puede fallar; esperamos a que cargue su adapter
        onView(withId(R.id.tracksRecyclerView))
            .perform(EspressoWaits.waitForRecyclerViewItemCount(minItemCount = 1, timeoutMs = 30_000))
        onView(withId(R.id.tracksRecyclerView))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun e2e_hu02_05_albumDetalle_botonAtrasRegresaAlCatalogo() {
        pressBack()
        onView(withId(R.id.rvAlbums))
            .check(matches(isDisplayed()))
    }
}

package com.misw.vinilos.performers

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
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
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PerformerDetailE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navegarAlDetalleDeArtista() {
        Thread.sleep(3000)
        onView(withId(R.id.PerformerListFragment)).perform(click())
        Thread.sleep(8000)
        onView(withId(R.id.rvPerformers))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Thread.sleep(5000)
    }

    @Test
    fun e2e_hu04_01_clickEnArtista_navegaAlDetalle() {
        onView(withId(R.id.performerName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu04_02_artistaDetalle_muestraNombre() {
        onView(withId(R.id.performerName))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun e2e_hu04_03_artistaDetalle_muestraDescripcion() {
        onView(withId(R.id.performerDescription))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu04_04_artistaDetalle_muestraAlbumesAsociados() {
        onView(withId(R.id.rvAlbums))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun e2e_hu04_05_artistaDetalle_clickEnAlbum_navegaAlDetalleDelAlbum() {
        onView(withId(R.id.rvAlbums))
            .perform(scrollTo())
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Thread.sleep(5000)
        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
    }
}

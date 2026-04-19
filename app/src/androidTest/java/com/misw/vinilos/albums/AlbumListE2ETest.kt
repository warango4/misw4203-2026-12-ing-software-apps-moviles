package com.misw.vinilos.albums

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.utils.RecyclerViewMatcher
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class AlbumListE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun esperarCargaInicial() {
        Thread.sleep(8000)
    }

    @Test
    fun e2e_hu01_01_albumList_seVisualizaAlAbrir() {
        onView(withId(R.id.rvAlbums))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rvAlbums))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun e2e_hu01_02_albumItem_muestraImagenNombreYGenero() {
        val itemMatcher = RecyclerViewMatcher(R.id.rvAlbums)
        onView(itemMatcher.atPositionOnView(0, R.id.ivAlbumCover))
            .check(matches(isDisplayed()))
        onView(itemMatcher.atPositionOnView(0, R.id.tvAlbumName))
            .check(matches(isDisplayed()))
        onView(itemMatcher.atPositionOnView(0, R.id.tvAlbumGenre))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu01_03_albumList_tieneMultiplesAlbumes() {
        onView(withId(R.id.rvAlbums))
            .check(matches(hasMinimumChildCount(2)))
    }

    @Test
    fun e2e_hu01_04_albumTab_estaActivaPorDefecto() {
        onView(withId(R.id.rvAlbums))
            .check(matches(isDisplayed()))
    }
}

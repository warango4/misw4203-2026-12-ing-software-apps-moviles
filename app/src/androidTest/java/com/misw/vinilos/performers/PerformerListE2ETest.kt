package com.misw.vinilos.performers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import com.misw.vinilos.testutils.EspressoIdlingRule
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class PerformerListE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val idlingRule = EspressoIdlingRule()

    @Before
    fun navegarALaPestanaArtistas() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
    }

    @Test
    fun e2e_hu03_01_performerTab_muestraListadoDeArtistas() {
        onView(withId(R.id.rvPerformers))
            .check(matches(isDisplayed()))
    }

    @Test
    fun e2e_hu03_02_performerList_tieneAlMenosUnArtista() {
        onView(withId(R.id.rvPerformers))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun e2e_hu03_03_performerList_incluyeMusicosYBandas() {
        onView(withId(R.id.rvPerformers))
            .check(matches(hasMinimumChildCount(2)))
    }

    @Test
    fun e2e_hu03_04_performerList_progressBarDesapareceTrasLaCarga() {
        onView(withId(R.id.progressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}

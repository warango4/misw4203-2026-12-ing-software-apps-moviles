package com.misw.vinilos.collectors

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.misw.vinilos.MainActivity
import com.misw.vinilos.R
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CollectorsEmptyStateE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun esperarCargaInicial() {
        Thread.sleep(8000)
    }

    @Test
    fun e2e_hu05_03_emptyState_noDebeVerseSiHayLista() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        Thread.sleep(3000)

        // En ambiente real normalmente hay coleccionistas; entonces el empty state debe estar oculto.
        // Si el backend devolviera vacío en algún momento, este test podría fallar (es un E2E).
        onView(withId(R.id.tvCollectorsEmpty))
            .check(matches(withEffectiveVisibility(GONE)))

        onView(withId(R.id.rvCollectors))
            .check(matches(isDisplayed()))
    }
}


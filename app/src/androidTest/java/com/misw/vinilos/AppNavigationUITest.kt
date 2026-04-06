package com.misw.vinilos

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testAlbumsTabClick_displaysAlbumsList() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun testPerformersTabClick_displaysPerformersList() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun testCollectorsTabClick_displaysCollectorsList() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }
}


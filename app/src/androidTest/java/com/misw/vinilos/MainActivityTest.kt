package com.misw.vinilos

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun bottomNavigation_isDisplayed() {
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToAlbums_isDisplayed() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToArtists_isDisplayed() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToCollectors_isDisplayed() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateAlbumsToArtists() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateArtistsToAlbums() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateAlbumsToCollectors() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateCollectorsToAlbums() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateArtistsToCollectors() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateCollectorsToArtists() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAlbumsMultipleTimes() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun clickArtistsMultipleTimes() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCollectorsMultipleTimes() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateSequenceAlbumsArtistsAlbums() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateSequenceArtistsCollectorsArtists() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateSequenceCollectorsAlbumsCollectors() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateAllTabsForward() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateAllTabsBackward() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun checkToolbarIsDisplayed() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyNavigationDoesNotHideToolbar() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAlbumsThenCheckToolbar() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun clickArtistsThenCheckToolbar() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCollectorsThenCheckToolbar() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateAlbumsArtistsCollectorsAlbums() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateCollectorsArtistsAlbumsCollectors() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyBottomNavigationIsVisibleAfterMultipleClicks() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateSequenceArtistsAlbumsCollectorsArtists() {
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAllNavigationItemsTwice() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyToolbarRemainsVisibleAfterComplexNavigation() {
        onView(withId(R.id.CollectorsFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateBackAndForthFast() {
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.AlbumListFragment)).perform(click())
        onView(withId(R.id.PerformerListFragment)).perform(click())
        onView(withId(R.id.rvPerformers)).check(matches(isDisplayed()))
    }
}

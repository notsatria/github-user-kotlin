package com.notsatria.githubusers.ui.main

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.notsatria.githubusers.utils.EspressoIdlingResource
import com.notsatria.githubusers.R
import com.notsatria.githubusers.ui.home.HomeFragment
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MainActivityTest {
    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
        ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }

    @Test
    fun testComponentShowed() {
        onView(withId(R.id.nav_host_fragment_activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.bottomNavbar)).check(matches(isDisplayed()))
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()))
    }

    @Test
    fun testNavigation() {
        onView(withId(R.id.navigation_favorite)).perform(click())
        onView(withId(R.id.navigation_setting)).perform(click())
        onView(withId(R.id.navigation_home)).perform(click())
    }
}
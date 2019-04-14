package com.nrojiani.githuborgsearch.controllers.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OrgDetailsFragmentTest {

    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun jumpToOrgDetailsFragment() {
        onView(withId(R.id.searchEditText)).perform(
            ViewActions.typeText("nytimes"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.searchButton)).perform(click())

        onView(withId(R.id.orgCardView))
            .perform(click())
    }

    @Test
    fun whenOrgDetailsFragmentLoaded_condensedOrgDetailsCardViewIsVisible() {
        // OrgDetailsFragment is now displayed
        onView(withId(R.id.condensedOrgView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.orgAvatarImageView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.condensedOrgNameTextView))
            .check(matches(isDisplayed()))
            .check(matches(withText("The New York Times")))

        onView(withId(R.id.condensedOrgLoginTextView))
            .check(matches(isDisplayed()))
            .check(matches(withText("@nytimes")))
    }

    @Test
    fun whenOrgDetailsFragmentLoaded_recyclerViewIsVisible() {
        // OrgDetailsFragment is now displayed
        onView(withId(R.id.condensedOrgView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.repoList))
            .check(matches(isDisplayed()))
    }

}
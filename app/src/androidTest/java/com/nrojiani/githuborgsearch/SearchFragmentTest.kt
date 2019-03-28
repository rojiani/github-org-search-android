package com.nrojiani.githuborgsearch

import android.app.Activity

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import com.nrojiani.githuborgsearch.controllers.fragments.SearchFragment


/**
 * UI Tests for SearchFragment
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchFragmentTest {

    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for
     * [androidx.test.rule.ActivityTestRule].
     */
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()


    @Test
    fun whenSearchEditTextIsEmpty_andSearchButtonClicked_showErrorMessage() {
        // Type (empty) text and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that the searchEditText's error message is displayed
        onView(withId(R.id.searchEditText)).check(
            matches(hasErrorText(SearchFragment.EMPTY_SEARCH_ERROR_MESSAGE))
        )
    }

    // search "foobar" (not found) -> Error: Not Found displayed

}

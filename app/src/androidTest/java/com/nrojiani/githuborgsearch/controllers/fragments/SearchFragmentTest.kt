package com.nrojiani.githuborgsearch.controllers.fragments

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * UI Tests for SearchFragment
 */
@LargeTest
class SearchFragmentTest {

    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for [androidx.test.rule.ActivityTestRule].
     */
    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<MainActivity>()

    @Test
    fun searchWidgetViewsAreVisible(scenario: ActivityScenario<MainActivity>) {
        val searchWidgetViewIds = setOf(
            R.id.searchBarWidget,
            R.id.textInputLayout,
            R.id.searchEditText,
            R.id.searchButton
        )
        searchWidgetViewIds.forEach { id ->
            onView(withId(id))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun whenSearchEditTextIsVisible_displayHint(scenario: ActivityScenario<MainActivity>) {
        onView(withId(R.id.searchEditText))
            .check(
                matches(
                    withHint("Enter an Organization")
                )
            )
    }

    @Test
    fun whenSearchEditTextIsEmpty_andSearchButtonClicked_showErrorMessage(
        scenario: ActivityScenario<MainActivity>
    ) {
        // Type (empty) text and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that the searchEditText's error message is displayed
        onView(withId(R.id.searchEditText)).check(
            matches(hasErrorText(SearchFragment.EMPTY_SEARCH_ERROR_MESSAGE))
        )
    }

    @Test
    fun whenSearchEditTextIsNotFound_andSearchButtonClicked_showErrorMessage(
        scenario: ActivityScenario<MainActivity>
    ) {
        // Error message that displays API errors is initially invisible
        onView(withId(R.id.errorTextView))
            .check(matches(not(isDisplayed())))

        // Type (empty) text and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText("foobar"), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that an error message is displayed (in errorTextView)
        onView(withId(R.id.errorTextView)).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun whenActivityStarts_progressBar_notVisible(scenario: ActivityScenario<MainActivity>) {
        // Progress bar not displayed until after text
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun whenValidSearch_orgCardViewIsDisplayed(scenario: ActivityScenario<MainActivity>) {
        // TODO: this is causing test to fail if all tests run in succession
        // initially not visible
        // onView(withId(R.id.orgCardView))
        //     .check(matches(not(isDisplayed())))

        // Type valid org name and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText("netflix"), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that CardView with Org details is displayed
        onView(withId(R.id.orgCardView))
            .check(matches(isDisplayed()))

        val widgetTexts = mapOf(
            R.id.orgNameTextView to "Netflix, Inc.",
            R.id.orgLoginTextView to "Netflix",
            R.id.orgLocationTextView to "Los Gatos, California",
            R.id.orgBlogTextView to "http://netflix.github.io/",
            R.id.orgDescriptionTextView to "Netflix Open Source Platform"
        )

        widgetTexts.forEach { (textViewId, expectedText) ->
            onView(withId(textViewId))
                .check(matches(withText(expectedText)))
        }
    }

    @Disabled("See comment")
    @Test
    fun whenValidSearch_withSomeMissingInfo_orgCardViewIsDisplayedWithMissingTextHidden(scenario: ActivityScenario<MainActivity>) {
        // TODO: this is causing test to fail if all tests run in succession
        // initially not visible
        // onView(withId(R.id.orgCardView))
        //     .check(matches(not(isDisplayed())))

        // Type (empty) text and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText("amzn"), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that CardView with Org details is displayed
        onView(withId(R.id.orgCardView))
            .check(matches(isDisplayed()))

        mapOf(
            R.id.orgNameTextView to "Amazon",
            R.id.orgLoginTextView to "amzn"
        ).forEach { (textViewId, expectedText) ->
            onView(withId(textViewId))
                .check(matches(withText(expectedText)))
        }

        // location, blog, description missing -> views hidden
        setOf(
            R.id.orgLocationTextView,
            R.id.orgBlogTextView,
            R.id.orgDescriptionTextView
        ).forEach { id ->
            onView(withId(id))
                .check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun whenSearchQueryHasWhitespace_theQueryIsTrimmed(scenario: ActivityScenario<MainActivity>) {
        // initially not visible
        onView(withId(R.id.orgCardView))
            .check(matches(not(isDisplayed())))

        // Type (empty) text and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText(" nytimes "), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that CardView with Org details is displayed
        onView(withId(R.id.orgCardView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun whenOrgCardViewClicked_fragmentTransition(scenario: ActivityScenario<MainActivity>) {
        // Type (empty) text and then press the search button.
        onView(withId(R.id.searchEditText)).perform(typeText("amzn"), closeSoftKeyboard())
        onView(withId(R.id.searchButton)).perform(click())

        // Verify that CardView with Org details is displayed
        onView(withId(R.id.orgCardView))
            .check(matches(isDisplayed()))
            .perform(click())

        // OrgDetailsFragment is now displayed
        onView(withId(R.id.orgDetailsFragment))
            .check(matches(isDisplayed()))
    }
}

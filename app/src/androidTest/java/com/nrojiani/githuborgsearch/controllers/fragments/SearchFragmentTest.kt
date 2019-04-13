package com.nrojiani.githuborgsearch.controllers.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import com.nrojiani.githuborgsearch.util.EspressoIdlingResource
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * UI Tests for SearchFragment
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class SearchFragmentTest {

    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for [androidx.test.rule.ActivityTestRule].
     */
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    /**
     * Source: [GitHub: googlesamples/android-architecture - todo-mvvm-live-kotlin](https://github.com/googlesamples/android-architecture/tree/todo-mvvm-live-kotlin)
     *
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests
     * significantly more reliable.
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }


    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun searchWidgetViewsAreVisible() {
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
    fun whenSearchEditTextIsVisible_displayHint() {
        onView(withId(R.id.searchEditText))
            .check(
                matches(
                    withHint("Enter an Organization")
                )
            )
    }

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

    @Test
    fun whenSearchEditTextIsNotFound_andSearchButtonClicked_showErrorMessage() {
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
    fun whenActivityStarts_progressBar_notVisible() {
        // Progress bar not displayed until after text
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun whenValidSearch_orgCardViewIsDisplayed() {
        // initially not visible
        onView(withId(R.id.orgCardView))
            .check(matches(not(isDisplayed())))

        // Type (empty) text and then press the search button.
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

    @Test
    fun whenValidSearch_withSomeMissingInfo_orgCardViewIsDisplayedWithMissingTextHidden() {
        // initially not visible
        onView(withId(R.id.orgCardView))
            .check(matches(not(isDisplayed())))

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
        )
            .forEach { id ->
                onView(withId(id))
                    .check(matches(not(isDisplayed())))
            }
    }

    @Test
    fun whenSearchQueryHasWhitespace_theQueryIsTrimmed() {
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
    fun whenOrgCardViewClicked_fragmentTransition() {
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
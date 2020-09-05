package com.nrojiani.githuborgsearch.controllers.fragments

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import de.mannodermaus.junit5.ActivityScenarioExtension
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * https://developer.android.com/guide/components/activities/testing
 * https://developer.android.com/reference/androidx/test/ext/junit/rules/ActivityScenarioRule
 * https://medium.com/stepstone-tech/better-tests-with-androidxs-activityscenario-in-kotlin-part-1-6a6376b713ea
 * https://github.com/mannodermaus/android-junit5/pull/155
 */
@LargeTest
class OrgDetailsFragmentTest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<MainActivity>()

    @Test
    fun whenOrgDetailsFragmentLoaded_condensedOrgDetailsCardViewIsVisible(
        scenario: ActivityScenario<MainActivity>
    ) {
        jumpToOrgDetailsFragment()
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
    fun whenOrgDetailsFragmentLoaded_expectedViewsAreDisplayed(
        scenario: ActivityScenario<MainActivity>
    ) {
        jumpToOrgDetailsFragment()
        // OrgDetailsFragment is now displayed
        onView(withId(R.id.condensedOrgView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.repoList))
            .check(matches(isDisplayed()))

        onView(withId(R.id.orgOwnsNoReposErrorMessage))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun whenOrgDetailsFragmentLoaded_andRepoListEmpty_errorMessageIsVisible(
        scenario: ActivityScenario<MainActivity>
    ) {
        jumpToOrgDetailsFragment(ORG_WITH_NO_REPOS)

        onView(withId(R.id.condensedOrgView))
            .check(matches(isDisplayed()))

        onView(withId(R.id.orgOwnsNoReposErrorMessage))
            .check(matches(isDisplayed()))
    }

    /**
     * Enter an organization and search so that OrgDetailsFragment is displayed.
     */
    private fun jumpToOrgDetailsFragment(orgName: String = DEFAULT_ORG) {
        onView(withId(R.id.searchEditText)).perform(
            ViewActions.typeText(orgName),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.searchButton)).perform(ViewActions.click())

        onView(withId(R.id.orgCardView))
            .perform(ViewActions.click())
    }

    companion object {
        private const val DEFAULT_ORG = "nytimes"
        private const val ORG_WITH_NO_REPOS = "nytime"
    }
}

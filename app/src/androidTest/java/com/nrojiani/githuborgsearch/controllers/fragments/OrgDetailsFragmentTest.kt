package com.nrojiani.githuborgsearch.controllers.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.nrojiani.githuborgsearch.R
import com.nrojiani.githuborgsearch.controllers.activities.MainActivity
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class OrgDetailsFragmentTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun whenOrgDetailsFragmentLoaded_condensedOrgDetailsCardViewIsVisible() {
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
            .check(matches(withText("nytimes")))
    }

    @Test
    fun whenOrgDetailsFragmentLoaded_expectedViewsAreDisplayed() {
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
    fun whenOrgDetailsFragmentLoaded_andRepoListEmpty_errorMessageIsVisible() {
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

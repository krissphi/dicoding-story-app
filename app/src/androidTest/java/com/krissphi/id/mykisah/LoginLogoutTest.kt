package com.krissphi.id.mykisah

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.krissphi.id.mykisah.ui.page.welcome.WelcomeActivity
import com.krissphi.id.mykisah.utils.EspressoIdlingResource
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginLogoutTest {
    @get:Rule
    val activity = ActivityScenarioRule(WelcomeActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }

    @Test
    fun loginLogoutScenario_Success() {
        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.edtEmail)).perform(typeText("tesq@gmail.com"))
        onView(withId(R.id.edtPassword)).perform(typeText("12345678"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

        onView(withId(R.id.action_logout)).perform(click())

        onView(allOf(withId(android.R.id.button1), withText(R.string.logout))).perform(click())

        onView(withId(R.id.tvHeading)).check(matches(withText(R.string.welcome)))
    }
}
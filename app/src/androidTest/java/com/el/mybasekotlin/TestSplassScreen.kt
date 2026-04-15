package com.el.mybasekotlin

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.el.mybasekotlin.di.AppModule
import com.el.mybasekotlin.ui.fragment.SplashScreenFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by ElChuanmen on 2/20/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@HiltAndroidTest
@UninstallModules(AppModule::class)
class MyFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(Frag::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testDisplayTextView() {
        val scenario = launchFragmentInContainer<SplashScreenFragment>()
        onView(withId(R.id.titleScreen))
            .check(matches(withText("Splash Screen")))
    }
}

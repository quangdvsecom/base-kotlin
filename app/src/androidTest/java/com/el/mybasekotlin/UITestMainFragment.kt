package com.el.mybasekotlin


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.el.mybasekotlin.ui.activities.MainActivity
import com.el.mybasekotlin.ui.fragment.LoginFragment
import com.el.mybasekotlin.ui.fragment.MainFragment
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by ElChuanmen on 2/17/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@RunWith(AndroidJUnit4::class)
class UITestMainFragment {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setup() {
        // Khởi chạy LoginFragment trong môi trường test
        activityRule.scenario.onActivity { activity ->
            val fragment = MainFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }
//    @Test
//    fun testLoginSuccess() {
//        // Nhập username + password đúng
//        onView(withId(R.id.etUsername)).perform(typeText("admin"), closeSoftKeyboard())
//        onView(withId(R.id.etPassword)).perform(typeText("1234"), closeSoftKeyboard())
//
//        // Bấm nút Login
//        onView(withId(R.id.btnLogin)).perform(click())
//
//        // Kiểm tra kết quả thành công
//        onView(withId(R.id.tvResult)).check(matches(withText("Login Success")))
//    }
}
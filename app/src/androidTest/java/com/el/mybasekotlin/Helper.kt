package com.el.mybasekotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario

/**
 * Created by ElChuanmen on 2/20/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
inline fun <reified F : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    crossinline action: F.() -> Unit = {}
) {
    val scenario = ActivityScenario.launch(HiltTestActivity::class.java)
    scenario.onActivity { activity ->
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            F::class.java.classLoader!!, F::class.java.name
        ) as F
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .commitNow()
        fragment.action()
    }
}
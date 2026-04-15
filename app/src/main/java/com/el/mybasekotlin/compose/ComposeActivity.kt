package com.el.mybasekotlin.compose


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.el.mybasekotlin.base.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.el.mybasekotlin.base.LocalGlobalViewModel

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {

    private val TAG: String = "MainActivity"
    private val TAG_LOG: String = "LogInformation"
    private val globalViewModel: GlobalViewModel by viewModels()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalGlobalViewModel provides globalViewModel) {
                DemoKotlinApp()
            }
        }


    }
}
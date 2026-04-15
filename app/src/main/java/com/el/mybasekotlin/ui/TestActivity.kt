package com.el.mybasekotlin.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key.Companion.Calculator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.el.mybasekotlin.R
import timber.log.Timber

class TestActivity : AppCompatActivity() {
    private var mathHelper=MathHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun init(){
        mathHelper.add(3,5)
    }



}
package com.el.mybasekotlin.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.el.mybasekotlin.databinding.LayoutCustomSnackBarBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout

/**
 * Custom snackBar
 */
object SnackBarUtil {
    @SuppressLint("RestrictedApi", "ShowToast")
    fun showSnackBar(
        context: Context,
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        val binding =
            LayoutCustomSnackBarBinding.inflate(
                LayoutInflater.from(context),
                view as ViewGroup,
                false
            )
        val snackBar = Snackbar.make(view, "", duration)
        snackBar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackBarLayout = snackBar.view as? SnackbarLayout
        binding.message.text = message
        snackBarLayout?.addView(binding.root, 0)
        snackBar.show()
    }

    fun showSnackBar(
        context: Context,
        view: View,
        @StringRes messageId: Int,
        duration: Int = Snackbar.LENGTH_SHORT
    ) {
        val message = context.getString(messageId)
        showSnackBar(context, view, message, duration)
    }
}

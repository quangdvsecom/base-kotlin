package com.el.mybasekotlin.ui.customview

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.el.mybasekotlin.R


class ToastUtil {
    private val LOADED_TOAST_TYPEFACE = Typeface.create("nunitosans_regular.ttf", Typeface.NORMAL)

    companion object {
        fun success(context: Context, title: String?): Toast? {
            return customDefault(
                context,
                Toast.LENGTH_LONG,
                R.drawable.ic_toast_success,
                R.drawable.bg_toast_success,
                title
            )
        }

        fun warning(context: Context, title: String?): Toast? {
            return customDefault(
                context,
                Toast.LENGTH_SHORT,
                R.drawable.ic_warning_toast,
                R.drawable.bg_toast_success,
                title
            )
        }

        fun error(context: Context, title: String?): Toast? {
            return customDefault(
                context,
                Toast.LENGTH_LONG,
                R.drawable.ic_error_toast,
                R.drawable.bg_toast_error,
                title
            )
        }

        fun customDefault(
            context: Context,
            duration: Int,
            icon: Int,
            background: Int,
            title: String?
        ): Toast? {
            val currentToast = Toast(context)
            //        ToastCustomBinding binding =ToastCustomBinding.inflate(LayoutInflater.from(context));
//        binding.icToast.setImageResource(icon);
//        binding.bgToast.setBackground(context.getDrawable(background));
//        binding.tvTitle.setText(title);
//        currentToast.setView(binding.getRoot());
            val toastLayout: View =
                (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(R.layout.toast_custom, null)
            val toastIcon = toastLayout.findViewById<ImageView>(R.id.ic_toast)
            val toastTextView = toastLayout.findViewById<TextView>(R.id.tvTitle)
            val view = toastLayout.findViewById<ConstraintLayout>(R.id.bg_toast)

            toastIcon.setImageResource(icon)
            view.background = context.getDrawable(background)
            toastTextView.text = title
            currentToast.duration = duration
            currentToast.view = toastLayout
            currentToast.setGravity(Gravity.TOP,0,0)
            return currentToast
        }

    }
}
package com.el.mybasekotlin.utils.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.Shader
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.el.mybasekotlin.utils.SafeOnClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

/**
 * Created by ChungHA
 */

fun View.hideKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

fun View.visibleIf(condition: Boolean, gone: Boolean = true) =
    if (condition) {
        visible()
    } else {
        if (gone) gone() else invisible()
    }

inline fun <reified T : View> ViewGroup.inflate(@LayoutRes resId: Int) =
    LayoutInflater.from(context).inflate(resId, this, false) as T

inline val ViewGroup.inflater: LayoutInflater get() = LayoutInflater.from(context)

val RecyclerView.hasItems: Boolean
    get() = (adapter?.itemCount ?: 0) > 0

fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    anchor: View? = null
) {
    Snackbar.make(this, message, duration)
        .setAnchorView(anchor)
        .show()
}

fun View.showSnackBar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    anchor: View? = null
) {
    Snackbar.make(this, textId, duration)
        .setAnchorView(anchor)
        .show()
}

fun View.showSnackBar(
    @StringRes textId: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
    @IdRes anchor: Int
) {
    Snackbar.make(this, textId, duration)
        .setAnchorView(anchor)
        .show()
}

fun Context.showSnackBar(
    view: View,
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(view, message, duration).show()
}

@SuppressLint("RestrictedApi")
fun Context.showSnackBarCustom(layoutId: Int, root: View) {
    // Create the Snackbar
    val snackbar = Snackbar.make(root, "", Snackbar.LENGTH_LONG)
    // Get the Snackbar's layout view
    val layout = snackbar.view as Snackbar.SnackbarLayout
    val snackView = LayoutInflater.from(this).inflate(layoutId, null)
    layout.setPadding(0, 0, 0, 0);
    // Add the view to the Snackbar's layout
    layout.addView(snackView, 0);
    // Show the Snackbar
    snackbar.show();
}

fun Context.openBrowser(
    url: String
) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    this.startActivity(browserIntent)
}

fun Context.openWebUrl(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    return try {
        startActivity(i)
    } catch (error: ActivityNotFoundException) {
    }
}

fun View.focusAndShowKeyboard() {
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        showTheKeyboardNow()
    } else {
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun View.onClick(safe: Boolean = true, action: (View) -> Unit) = setOnClickListener(
    SafeOnClickListener(safe, action)
)

fun View.onLongClick(action: (View) -> Unit) = setOnLongClickListener {
    action(it)
    true
}

@SuppressLint("ClickableViewAccessibility")
fun View.hideKeyboardClickOutSide() {
    fun View.setUpTouchListener(view: View) {
        if (view !is EditText || view !is ImageView) {
            view.setOnTouchListener { _, _ ->
                this.hideKeyboard()
                false
            }
        }
    }

    if (this is ViewGroup) {
        for (i in 0 until this.childCount) {
            val innerView = this.getChildAt(i)
            setUpTouchListener(innerView)
        }
    }
}

fun RecyclerView.getLastVisibleItemPosition(lastPosition: (Int) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val lastVisibleItem =
                    (layoutManager as LinearLayoutManager?)?.findLastCompletelyVisibleItemPosition()
                Timber.d("Pos rv: $lastVisibleItem")
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val lastVisibleItem =
                (layoutManager as LinearLayoutManager?)?.findLastVisibleItemPosition()
            lastPosition.invoke(lastVisibleItem ?: 0)
        }
    })
}

val View.isKeyboardShown: Boolean
    get() {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height

        // rect.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight > screenHeight * 0.15
    }

fun addKeyboardVisibilityListener(rootLayout: View): Flow<Boolean> {
    return callbackFlow {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            rootLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootLayout.rootView.height
            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom
            val isVisible =
                keypadHeight > screenHeight * 0.15 // 0.15 ratio is perhaps enough to determine keypad height.
            trySend(isVisible)
        }
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener(listener)
        awaitClose {
            rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}

inline fun View.doOnNextLayout(crossinline action: (view: View) -> Unit): View.OnLayoutChangeListener {
    return object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int,
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    }.also(this::addOnLayoutChangeListener)
}

suspend inline fun View.awaitNextLayout() = suspendCancellableCoroutine<Unit> { cont ->
    val listener = doOnNextLayout { cont.resume(Unit) }
    cont.invokeOnCancellation {
        removeOnLayoutChangeListener(listener)
    }
}

fun AppCompatTextView.setGradientText(primaryColor: String, secondaryColor: String, value: String) {
    paint.shader = LinearGradient(
        0f, 0f, width.toFloat(), height.toFloat(),
        Color.parseColor(primaryColor),
        Color.parseColor(secondaryColor),
        Shader.TileMode.CLAMP
    )
    text = value
}

fun View.goneDelay(handler: Handler, delay: Long) {
    handler.postDelayed({
        gone()
    }, delay)
}

fun RadioGroup.getCheckedRadioButtonPosition(): Int {
    val radioButtonId = checkedRadioButtonId
    return children.filter { it is RadioButton }.mapIndexed { index: Int, view: View ->
        index to view
    }.firstOrNull {
        it.second.id == radioButtonId
    }?.first ?: -1
}

fun View.fadeIn(duration: Long = 500) {
    this.visibility = View.VISIBLE
    val animation = AlphaAnimation(0f, 1f)
    animation.duration = duration
    this.startAnimation(animation)
}

fun View.downToTopVisible(duration: Long = 300) {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE else return
    val animate = TranslateAnimation(
        0f,                 // fromXDelta
        0f,                 // toXDelta
        this.height.toFloat(),  // fromYDelta
        0f                  // toYDelta
    )
    animate.duration = duration
    animate.fillAfter = false
    this.startAnimation(animate)
}

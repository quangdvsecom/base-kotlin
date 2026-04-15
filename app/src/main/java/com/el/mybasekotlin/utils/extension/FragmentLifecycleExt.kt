package com.el.mybasekotlin.utils.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

// For collect many flow state
// Follow lifecycle
fun Fragment.launchAndRepeatStarted(
    vararg launchBlock: suspend () -> Unit, doAfterLaunch: (() -> Unit)? = null
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launchBlock.forEach {
                launch { it.invoke() }
            }
            doAfterLaunch?.invoke()
        }
    }
}


inline fun <T> Flow<T>.collectInOwner(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (value: T) -> Unit,
): Job = owner.lifecycleScope.launch {
    owner.lifecycle.repeatOnLifecycle(state = minActiveState) {
        Timber.d("Start collecting...")
        collect { action(it) }
    }
}

fun FragmentActivity.launchAndRepeatStarted(
    vararg launchBlock: suspend () -> Unit,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(minActiveState) {
            launchBlock.forEach {
                launch { it.invoke() }
            }
        }
    }
}

inline fun <T> Flow<T>.collectIn(
    fragment: Fragment,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend (value: T) -> Unit,
): Job = fragment.viewLifecycleOwner.lifecycleScope.launch {
    fragment.viewLifecycleOwner.repeatOnLifecycle(state = minActiveState) {
        Timber.d("Start collecting...")
        collect { action(it) }
    }
}


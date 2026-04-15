package com.el.mybasekotlin.utils.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Collect with lifecycle
 *
 * @param T
 * @param lifecycleOwner
 * @param minActiveState
 * @param action
 * @receiver
 * @return
 */
inline fun <reified T> Flow<T>.launchAndCollectIn(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit = {}
): Job = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
        collect(action)
    }
}

inline fun <reified T> Flow<T>.launchAndCollectInActivity(
    lifecycleCoroutineScope: LifecycleCoroutineScope,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit = {}
): Job = lifecycleCoroutineScope.launch {
    lifecycleCoroutineScope.launchWhenStarted {
        collect(action)
    }
}


inline fun <reified T> Flow<T>.launchAndCollectEachIn(
    lifecycleOwner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    noinline action: suspend (T) -> Unit = {}
): Job = lifecycleOwner.lifecycleScope.launch {
    lifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
        onEach(action)
    }
}

fun <T> LifecycleOwner.execute(
    flow: Flow<T>,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit = {}
): Job = lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(minActiveState) {
        flow.collect(action)
    }
}

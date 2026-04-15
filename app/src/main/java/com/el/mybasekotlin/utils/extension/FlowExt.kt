package com.el.mybasekotlin.utils.extension

import android.os.Looper
import android.os.SystemClock
import android.view.View
import androidx.annotation.CheckResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Projects each source value to a [Flow] which is merged in the output [Flow] only if the previous projected [Flow] has completed.
 * If value is received while there is some projected [Flow] sequence being merged it will simply be ignored.
 *
 * This method is a shortcut for `map(transform).com.el.mybasekotlin.utils.extension.flattenFirst()`. See [flattenFirst].
 *
 * ### Operator fusion
 *
 * Applications of [flowOn], [buffer], and [produceIn] _after_ this operator are fused with
 * its concurrent merging so that only one properly configured channel is used for execution of merging logic.
 *
 * @param transform A transform function to apply to value that was observed while no Flow is executing in parallel.
 */
@ExperimentalCoroutinesApi
public fun <T, R> Flow<T>.flatMapFirst(transform: suspend (value: T) -> Flow<R>): Flow<R> =
    map(transform).flattenFirst()

/**
 * Converts a higher-order [Flow] into a first-order [Flow] by dropping inner [Flow] while the previous inner [Flow] has not yet completed.
 */
@ExperimentalCoroutinesApi
public fun <T> Flow<Flow<T>>.flattenFirst(): Flow<T> = channelFlow {
    val outerScope = this
    val busy = AtomicBoolean(false)

    collect { inner ->
        if (busy.compareAndSet(false, true)) {
            // Do not pay for dispatch here, it's never necessary
            launch(start = CoroutineStart.UNDISPATCHED) {
                try {
                    inner.collect { outerScope.send(it) }
                    busy.set(false)
                } catch (e: CancellationException) {
                    // cancel outer scope on cancellation exception, too
                    outerScope.cancel(e)
                }
            }
        }
    }
}

/**
 * Emits the given constant value on the output Flow every time the source Flow emits a value.
 *
 * @param value The value to map each source value to.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <T, R> Flow<T>.mapTo(value: R): Flow<R> =
    transform { return@transform emit(value) }

/**
 * @param value . combine default max 5 flow, i want to handle 6 flow.
 */

fun <T1, T2, T3, T4, T5, T6, R> combine6Flow(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = combine(
    combine(flow, flow2, flow3, ::Triple),
    combine(flow4, flow5, flow6, ::Triple)
) { t1, t2 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t2.third
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, R> combine7Flow(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R,
): Flow<R> = combine(
    combine(flow, flow2, flow3) { t1, t2, t3 -> Triple(t1, t2, t3) },
    combine(flow4, flow5) { t1, t2 -> Pair(t1, t2) },
    combine(flow6, flow7) { t1, t2 -> Pair(t1, t2) },
) { t1, t2, t3 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t3.first,
        t3.second
    )
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine8Flow(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    flow7: Flow<T7>,
    flow8: Flow<T8>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
): Flow<R> = combine(
    combine(flow, flow2, flow3) { t1, t2, t3 -> Triple(t1, t2, t3) },
    combine(flow4, flow5) { t1, t2 -> Pair(t1, t2) },
    combine(flow6, flow7, flow8) { t1, t2, t3 -> Triple(t1, t2, t3) },
) { t1, t2, t3 ->
    transform(
        t1.first,
        t1.second,
        t1.third,
        t2.first,
        t2.second,
        t3.first,
        t3.second,
        t3.third
    )
}


/**
 * Converts a [Flow] to an [InitialValueFlow], taking an [initialValue] lambda for computing the initial value.
 */
public fun <T : Any> Flow<T>.asInitialValueFlow(initialValue: () -> T): InitialValueFlow<T> =
    InitialValueFlow(
        onStart {
            emit(initialValue())
        }
    )

/**
 * A [Flow] implementation that emits the current value of a widget immediately upon collection.
 */
public class InitialValueFlow<T : Any>(private val flow: Flow<T>) : Flow<T> by flow {

    /**
     * Returns a [Flow] that skips the initial emission of the current value.
     */
    public fun skipInitialValue(): Flow<T> = flow.drop(1)
}

public fun checkMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "Expected to be called on the main thread but was " + Thread.currentThread().name
    }
}

@CheckResult
@OptIn(ExperimentalCoroutinesApi::class)
fun View.onClicked(): Flow<Unit> = callbackFlow {
    checkMainThread()
    val listener = View.OnClickListener {
        trySend(Unit)
    }
    setOnClickListener(listener)
    awaitClose { setOnClickListener(null) }
}.conflate()

fun <T> MutableStateFlow<T?>.set(value: T, default: T? = null) {
    this.value = value
    this.value = default
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.throttle(timeoutMillis: (T) -> Long): Flow<T> {
    var lastEmittedAt = 0L
    return transformLatest { value ->
        val delay = timeoutMillis(value)
        val now = SystemClock.elapsedRealtime()
        if (delay > 0L) {
            if (lastEmittedAt + delay < now) {
                delay(lastEmittedAt + delay - now)
            }
        }
        emit(value)
        lastEmittedAt = now
    }
}

fun <T> Flow<T>.throttleFlow(waitMillis: Int) = flow {
    coroutineScope {
        val context = coroutineContext
        var nextMillis = 0L
        var delayPost: Deferred<Unit>? = null
        collect {
            val current = SystemClock.uptimeMillis()
            if (nextMillis < current) {
                nextMillis = current + waitMillis
                emit(it)
                delayPost?.cancel()
            } else {
                val delayNext = nextMillis
                delayPost?.cancel()
                delayPost = async(Dispatchers.Default) {
                    delay(nextMillis - current)
                    if (delayNext == nextMillis) {
                        nextMillis = SystemClock.uptimeMillis() + waitMillis
                        withContext(context) {
                            emit(it)
                        }
                    }
                }
            }
        }
    }
}

fun <T> throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}

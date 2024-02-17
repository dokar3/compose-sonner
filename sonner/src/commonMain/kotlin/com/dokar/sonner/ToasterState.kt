package com.dokar.sonner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

/**
 * Create a [ToasterState] and remember it.
 *
 * @param onToastDismissed A callback will be called when any toast is dismissed.
 */
@Composable
fun rememberToasterState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onToastDismissed: ((toast: Toast) -> Unit)? = null,
): ToasterState {
    return remember(coroutineScope) {
        ToasterState(
            coroutineScope = coroutineScope,
            onDismissed = onToastDismissed,
        )
    }.also {
        it.onDismissed = onToastDismissed
    }
}

/**
 * The toaster state, used to show and dismiss toasts.
 *
 * ### Examples:
 *
 * ```kotlin
 * // Show a simple toast
 * val toast = toaster.show("Message")
 *
 * // Show a toast with some parameters
 * toaster.show(
 *     message = "Message",
 *     type = ToastType.Error,
 *     duration = ToasterDefaults.DurationLong,
 * )
 *
 * // Dismiss a toast
 * toaster.dismiss(toast)
 *
 * // Dismiss a toast by id
 * toaster.dismiss(id)
 *
 * // Dismiss all toasts
 * toaster.dismissAll()
 * ```
 */
@Stable
class ToasterState(
    private val coroutineScope: CoroutineScope,
    internal var onDismissed: ((toast: Toast) -> Unit)? = null,
) {
    private val _toasts = mutableStateListOf<StatefulToast>()
    internal val toasts: List<StatefulToast> = _toasts

    private val jobs = mutableMapOf<Any, Job>()

    /**
     * Show a toast from the parameters of [Toast].
     */
    fun show(
        message: Any,
        id: Any = currentNanoTime(),
        icon: Any? = null,
        action: Any? = null,
        type: ToastType = ToastType.Normal,
        duration: Duration = ToasterDefaults.DurationDefault,
    ): Toast {
        val toast = Toast(
            id = id,
            icon = icon,
            message = message,
            action = action,
            type = type,
            duration = duration,
        )
        updateOrShow(toast)
        return toast
    }

    /**
     * Show a toast.
     */
    fun show(toast: Toast) {
        updateOrShow(toast)
    }

    /**
     * Dismiss a toast.
     */
    fun dismiss(toast: Toast) {
        dismiss(toast.id)
    }

    /**
     * Dismiss a toast by id.
     */
    fun dismiss(id: Any) {
        updateToast(id) {
            if (it.isVisible) it.copy(state = VisibleState.Dismissing) else it
        }
        for ((toastId, job) in jobs) {
            if (toastId == id) {
                job.cancel()
                break
            }
        }
    }

    /**
     * Dismiss all toasts.
     */
    fun dismissAll() {
        for (i in 0..toasts.lastIndex) {
            updateToast(toasts[i].toast.id) {
                if (it.isVisible) it.copy(state = VisibleState.Dismissing) else it
            }
        }
        jobs.forEach { (_, job) -> job.cancel() }
    }

    private fun updateOrShow(toast: Toast) {
        val index = toasts.indexOfFirst { it.toast.id == toast.id }
        if (index != -1) {
            val updated = toasts[index].copy(toast = toast, state = VisibleState.Visible)
            _toasts[index] = updated
            startToastJob(toast = toast, displayedTime = updated.displayedTime)
        } else {
            _toasts.add(StatefulToast(toast))
            startToastJob(toast = toast, displayedTime = Duration.ZERO)
        }
    }

    private fun startToastJob(toast: Toast, displayedTime: Duration) {
        val id = toast.id
        jobs[id]?.cancel()
        var delayed = displayedTime
        jobs[id] = coroutineScope.launch {
            var delayDuration = toast.duration
            while (isActive) {
                delay(delayDuration)
                delayed += delayDuration
                // Always get the latest duration
                val expectedDuration = toasts.find { it.toast.id == id }
                    ?.toast?.duration
                    ?: toast.duration
                if (delayed >= expectedDuration) {
                    break
                }
                delayDuration = expectedDuration - delayed
            }
            updateToast(id) { it.copy(state = VisibleState.Dismissing) }
        }.also { job ->
            job.invokeOnCompletion {
                updateToast(id) { it.copy(displayedTime = delayed) }
                // Make sure to remove the job from the map
                if (jobs[id] == job) {
                    jobs.remove(id)
                }
            }
        }
    }

    internal fun markDismissed(id: Any) {
        val index = toasts.indexOfFirst { it.toast.id == id }
        if (index != -1) {
            // Don't remove toast directly, that will break our animations!!!
            updateToast(id) { it.copy(state = VisibleState.Dismissed) }
            onDismissed?.invoke(toasts[index].toast)
            if (jobs[id]?.isActive == true) {
                jobs[id]?.cancel()
            }
            clearToastsIfAllDismissed()
        }
    }

    internal fun pauseDismissTimer(id: Any) {
        val job = jobs[id] ?: return
        job.cancel()
    }

    internal fun resumeDismissTimer(id: Any) {
        if (jobs[id]?.isActive == true) return
        val statefulToast = toasts.firstOrNull { it.toast.id == id } ?: return
        if (statefulToast.displayedTime >= statefulToast.toast.duration) return
        startToastJob(statefulToast.toast, displayedTime = statefulToast.displayedTime)
    }

    internal fun invisibleItemsInRangeFlow(start: Int, end: Int): Flow<Int> {
        fun isOutOfBounds(list: List<*>, start: Int, end: Int): Boolean {
            val indices = list.indices
            return start !in indices || end !in indices
        }

        return snapshotFlow { toasts.map { it.isDismissing || it.isDismissed } }
            .map { list ->
                var count = 0
                if (isOutOfBounds(list, start, end)) {
                    return@map 0
                }
                for (i in start..end) {
                    if (list[i]) {
                        count++
                    }
                }
                count
            }
    }

    private inline fun updateToast(id: Any, block: (current: StatefulToast) -> StatefulToast) {
        val index = toasts.indexOfFirst { it.toast.id == id }
        if (index == -1) return
        val current = toasts[index]
        val updated = block(current)
        if (updated != current) {
            _toasts[index] = updated
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    internal fun dismissingToastsFlow(): Flow<StatefulToast> {
        return snapshotFlow { toasts.map { it to it.isDismissing } }
            .flatMapMerge { flowOf(*it.toTypedArray()) }
            .filter { it.second }
            .map { it.first }
    }

    private fun clearToastsIfAllDismissed() {
        if (toasts.all { it.isDismissed }) {
            _toasts.clear()
        }
    }
}

internal data class StatefulToast(
    val toast: Toast,
    val state: VisibleState = VisibleState.Visible,
    val displayedTime: Duration = Duration.ZERO,
) {
    val isVisible get() = state == VisibleState.Visible
    val isDismissing get() = state == VisibleState.Dismissing
    val isDismissed get() = state == VisibleState.Dismissed
}

internal enum class VisibleState {
    Visible,
    Dismissing,
    Dismissed,
}

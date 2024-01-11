package com.dokar.sonner

import androidx.compose.runtime.State
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.jvm.JvmName

/**
 * Listen to the toast state, show/update when the toast is added/updated,
 * and dismiss when it's null.
 *
 * This requires reading the [State] value in the block instead of the normal value.
 */
@JvmName("listenToState")
suspend inline fun ToasterState.listen(crossinline readStateBlock: () -> Toast?) {
    listen(snapshotFlow { readStateBlock() })
}

/**
 * Listen to the toast collection state, show/update when toasts are added/updated,
 * and dismiss toasts when removed.
 *
 * This requires reading the [State] value in the block instead of the normal value.
 */
@JvmName("listenToStateMany")
suspend inline fun ToasterState.listenMany(crossinline readStateBlock: () -> Iterable<Toast>) {
    listenMany(snapshotFlow { readStateBlock() })
}

/**
 * Listen to the toast flow, show/update when the toast is added/updated,
 * and dismiss when the flow emits null.
 */
@JvmName("listenToFlow")
suspend fun ToasterState.listen(flow: Flow<Toast?>) {
    val mapped = flow.map { if (it != null) listOf(it) else emptyList() }
    listenMany(flow = mapped)
}

/**
 * Listen to the toast collection flow, show/update when toasts are added/updated,
 * and dismiss when toasts are removed.
 */
@JvmName("listenToFlowMany")
suspend fun ToasterState.listenMany(flow: Flow<Iterable<Toast>>) {
    var previousMap = mutableMapOf<Any, Toast>()
    flow.collect { newList ->
        // Append or update
        val newMap = mutableMapOf<Any, Toast>()
        for (toast in newList) {
            newMap[toast.id] = toast
            val previous = previousMap[toast.id]
            if (previous == null || toast != previous) {
                show(toast)
            }
        }
        // Remove
        for ((id, _) in previousMap) {
            if (!newMap.contains(id)) {
                dismiss(id)
            }
        }
        previousMap = newMap
    }
}

package com.dokar.sonner.test

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import com.dokar.sonner.Toast
import com.dokar.sonner.listen
import com.dokar.sonner.listenMany
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ToasterStateExtTest {
    @Test
    fun listenSingleInFlows() = testToasterState { toaster ->
        val channel = Channel<Toast?>()
        launch { toaster.listen(channel.receiveAsFlow()) }

        // Emit toast
        channel.send(Toast("Hello"))
        assertNotNull(toaster.toasts.find { it.toast.message == "Hello" && it.isVisible })

        // Dismiss
        channel.send(null)
        yield()
        assertNull(toaster.toasts.find { it.toast.message == "Hello" && it.isVisible })

        channel.cancel()
    }

    @Test
    fun listenMultipleInFlows() = testToasterState { toaster ->
        val channel = Channel<List<Toast>>()
        launch { toaster.listenMany(channel.receiveAsFlow()) }

        // Emit toasts
        val toasts = listOf(Toast("Hello"), Toast("World"))
        channel.send(toasts)
        assertTrue(toaster.toasts.map { it.toast }.containsAll(toasts))

        // Dismiss the second toast
        channel.send(listOf(toasts[0]))
        yield()
        assertNotNull(toaster.toasts.find { it.toast.message == "Hello" && it.isVisible })
        assertNotNull(toaster.toasts.find { it.toast.message == "World" && it.isDismissing })

        // Dismiss all
        channel.send(emptyList())
        yield()
        assertTrue(toaster.toasts.all { it.isDismissing })

        channel.cancel()
    }

    @Test
    fun listenSingleInStates() = testToasterState { toaster ->
        val state = mutableStateOf<Toast?>(null)
        val job = launch { toaster.listen { state.value } }

        // Emit toast
        state.value = Toast("Hello")
        Snapshot.sendApplyNotifications()
        yield()
        assertNotNull(toaster.toasts.find { it.toast.message == "Hello" && it.isVisible })

        // Dismiss
        state.value = null
        Snapshot.sendApplyNotifications()
        yield()
        assertNotNull(toaster.toasts.find { it.toast.message == "Hello" && it.isDismissing })

        job.cancel()
    }

    @Test
    fun listenMultipleInStates() = testToasterState { toaster ->
        val state = mutableStateOf<List<Toast>>(emptyList())
        val job = launch { toaster.listenMany { state.value } }

        // Emit toasts
        val toasts = listOf(Toast("Hello"), Toast("World"))
        state.value = toasts
        Snapshot.sendApplyNotifications()
        yield()
        assertTrue(toaster.toasts.map { it.toast }.containsAll(toasts))

        // Dismiss the second toast
        state.value = listOf(toasts[0])
        Snapshot.sendApplyNotifications()
        yield()
        assertNotNull(toaster.toasts.find { it.toast.message == "World" && it.isDismissing })

        // Dismiss the rest toasts
        state.value = emptyList()
        Snapshot.sendApplyNotifications()
        yield()
        assertTrue(toaster.toasts.all { it.isDismissing })

        job.cancel()
    }
}
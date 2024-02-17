package com.dokar.sonner.test

import com.dokar.sonner.Toast
import com.dokar.sonner.ToasterState
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.Duration.Companion.milliseconds

class ToasterStateTest {
    @Test
    fun showAndDismiss() = testToasterState { toaster ->
        val duration = 1000.milliseconds
        toaster.show("Hello", duration = duration)
        assertNotNull(toaster.toasts.find { it.toast.message == "Hello" })

        delay(duration + 50.milliseconds)

        toaster.markAllDismissingToDismissed()
        assertTrue(toaster.toasts.isEmpty())
    }

    @Test
    fun dismiss() = testToasterState { toaster ->
        val toast = toaster.show("Message", duration = INFINITE)
        toaster.dismiss(toast)
        toaster.markAllDismissingToDismissed()
        assertNull(toaster.toasts.find { it.toast.message == "Message" })

        val id = toaster.show("Message", duration = INFINITE).id
        toaster.dismiss(id)
        toaster.markAllDismissingToDismissed()
        assertNull(toaster.toasts.find { it.toast.message == "Message" })
    }

    @Test
    fun dismissAll() = testToasterState { toaster ->
        toaster.show("1")
        toaster.show("2")
        toaster.show("3")
        toaster.dismissAll()
        toaster.markAllDismissingToDismissed()
        assertTrue(toaster.toasts.isEmpty())
    }

    @Test
    fun updateMessage() = testToasterState { toaster ->
        val toast = toaster.show("Message")
        toaster.show(message = "Updated message", id = toast.id)
        assertNull(toaster.toasts.find { it.toast.message == "Message" })
        assertEquals("Updated message", toaster.toasts.first().toast.message)
    }

    @Test
    fun updateDuration() = testToasterState { toaster ->
        val toast = toaster.show(message = "", duration = 1000.milliseconds)
        delay(800)
        toaster.show(message = "", id = toast.id, duration = 2000.milliseconds)
        delay(300)

        toaster.markAllDismissingToDismissed()
        // Should last another 1700ms
        assertNotNull(toaster.toasts.find { it.toast.id == toast.id })

        delay(1700)

        toaster.markAllDismissingToDismissed()
        // Should be dismissed
        assertNull(toaster.toasts.find { it.toast.id == toast.id })
    }

    @Test
    fun onToastDismissedCallback() {
        val dismissedToasts = mutableListOf<Toast>()
        testToasterState(onToastDismissed = dismissedToasts::add) { toaster ->
            val toasts = listOf(
                toaster.show("The"),
                toaster.show("Next"),
                toaster.show("Message"),
                toaster.show("."),
            )
            toaster.dismissAll()
            toaster.markAllDismissingToDismissed()
            assertTrue(dismissedToasts.containsAll(toasts))
        }
    }
}

internal fun testToasterState(
    onToastDismissed: ((toast: Toast) -> Unit)? = null,
    block: suspend TestScope.(toaster: ToasterState) -> Unit
) {
    runTest {
        val state = ToasterState(onDismissed = onToastDismissed, coroutineScope = this)
        block(state)
        state.markAllDismissingToDismissed()
    }
}

/**
 * This method simulates the ui actions when exit transitions are finished.
 */
internal fun ToasterState.markAllDismissingToDismissed() {
    for (i in toasts.indices) {
        if (i > toasts.lastIndex) break
        val item = toasts[i]
        if (item.isDismissing) {
            markDismissed(id = item.toast.id)
        }
    }
}
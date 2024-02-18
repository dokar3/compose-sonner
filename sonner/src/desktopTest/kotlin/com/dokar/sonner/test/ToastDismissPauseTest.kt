package com.dokar.sonner.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.Toast
import com.dokar.sonner.ToastDismissPause
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterDefaults
import com.dokar.sonner.ToasterState
import com.dokar.sonner.listenMany
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.math.min
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class ToastDismissPauseTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun neverPause() = runTest {
        val maxVisibleToasts = 3
        val toasts = setupToasterAndVerify(
            dismissPause = ToastDismissPause.Never,
            maxVisibleToasts = maxVisibleToasts,
        )

        advanceTimeBy(ToasterDefaults.DurationDefault + 50.milliseconds)

        // All toasts should be dismissed
        toasts.forEach {
            composeTestRule.onNodeWithText(it.message.toString()).assertDoesNotExist()
        }
    }

    @Test
    fun pauseOnNotFront() = runTest {
        val maxVisibleToasts = 3
        val toasts = setupToasterAndVerify(
            dismissPause = ToastDismissPause.OnNotFront,
            maxVisibleToasts = maxVisibleToasts,
        )

        // Toasts should be dismissed one by one
        val reversed = toasts.reversed().map { it.message.toString() }
        for (i in toasts.indices) {
            advanceTimeBy(ToasterDefaults.DurationDefault + 50.milliseconds)
            // The front toast should be dismissed
            composeTestRule.onNodeWithText(reversed[i]).assertDoesNotExist()
            // Others will still visible
            val visibleStart = i + 1
            val visibleEnd = min(toasts.lastIndex, visibleStart + maxVisibleToasts)
            for (j in visibleStart..visibleEnd) {
                composeTestRule.onNodeWithText(reversed[j]).assertIsDisplayed()
            }
        }
    }

    @Test
    fun pauseOnNotFrontWithListenMany() = runTest {
        val idsFlow = MutableStateFlow(listOf(1, 2, 3, 4, 5, 6))
        val toaster = ToasterState(
            coroutineScope = this,
            onDismissed = {
                idsFlow.update { list -> list - (it.id as Int) }
            },
        )
        val maxVisibleToasts = 3

        composeTestRule.setContent {
            LaunchedEffect(toaster) {
                toaster.listenMany(
                    idsFlow.map { ids ->
                        // This will delay the dismiss timers and make the test fail,
                        // if we don't pause timers after new timers are started due
                        // to the updates.
                        // In tests, timers will be started before the pauses, but on
                        // devices, this barely happens.
                        delay(10)
                        ids.map { id ->
                            Toast(
                                id = id,
                                message = "Toast: $id",
                                duration = ToasterDefaults.DurationShort,
                                action = TextToastAction(
                                    text = "Dismiss",
                                    onClick = { toaster.dismiss(it.id) }
                                )
                            )
                        }
                    }
                )
            }

            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    dismissPause = ToastDismissPause.OnNotFront,
                    maxVisibleToasts = maxVisibleToasts,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        // Toasts should be dismissed one by one
        val messages = idsFlow.value.reversed().map { "Toast: $it" }
        for (i in messages.indices) {
            advanceTimeBy(ToasterDefaults.DurationShort + 50.milliseconds)
            // The front toast should be dismissed
            composeTestRule.onNodeWithText(messages[i]).assertDoesNotExist()
            // Others will still visible
            val visibleStart = i + 1
            val visibleEnd = min(messages.lastIndex, visibleStart + maxVisibleToasts)
            for (j in visibleStart..visibleEnd) {
                composeTestRule.onNodeWithText(messages[j]).assertIsDisplayed()
            }
        }
    }

    @Test
    fun pauseOnInvisible() = runTest {
        val maxVisibleToasts = 3
        val toasts = setupToasterAndVerify(
            dismissPause = ToastDismissPause.OnInvisible,
            maxVisibleToasts = maxVisibleToasts,
        )

        // Visible toasts will be dismissed first
        val reversed = toasts.reversed().map { it.message.toString() }
        var start = 0
        while (start <= toasts.lastIndex) {
            advanceTimeBy(ToasterDefaults.DurationDefault + 50.milliseconds)
            val end = min(toasts.lastIndex, start + maxVisibleToasts - 1)
            for (i in start..end) {
                composeTestRule.onNodeWithText(reversed[i]).assertDoesNotExist()
            }
            for (i in (end + 1)..toasts.lastIndex) {
                composeTestRule.onNodeWithText(reversed[i]).assertIsDisplayed()
            }
            start += maxVisibleToasts
        }
    }

    private fun TestScope.setupToasterAndVerify(
        dismissPause: ToastDismissPause,
        maxVisibleToasts: Int,
        toastCount: Int = maxVisibleToasts * 2,
    ): List<Toast> {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    dismissPause = dismissPause,
                    maxVisibleToasts = maxVisibleToasts,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        val toastMessages = List(toastCount) { "Toast $it" }
        val toasts = toastMessages.map { toaster.show(it) }
        toastMessages
            .subList(toastMessages.size - maxVisibleToasts, toastMessages.size)
            .forEach { composeTestRule.onNodeWithText(it).assertIsDisplayed() }
        return toasts
    }
}
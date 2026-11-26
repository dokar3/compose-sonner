package com.dokar.sonner.test

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.ToastSwipeDirections
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ToasterTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showAndDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()

        toaster.dismissAll()
        composeTestRule.onNodeWithText("Hello").assertDoesNotExist()
    }

    @Test
    fun updateToastMessage() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello", id = 0)
        composeTestRule.onNodeWithText("Hello").assertIsDisplayed()

        toaster.show("World", id = 0)
        composeTestRule.onNodeWithText("Hello").assertDoesNotExist()
        composeTestRule.onNodeWithText("World").assertIsDisplayed()
    }

    @Test
    fun maxVisibleToasts() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    maxVisibleToasts = 2,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("This")
        toaster.show("Is")
        toaster.show("A")
        toaster.show("Toaster")
        composeTestRule.onNodeWithText("This").assertDoesNotExist()
        // This toast is displayed but invisible to the user to avoid animation clipping
        composeTestRule.onNodeWithText("Is").assertIsDisplayed()
        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Toaster").assertIsDisplayed()
    }

    @Test
    fun isIconDisplayed() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    maxVisibleToasts = 2,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Failed", type = ToastType.Error)
        composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    }

    @Test
    fun doesCloseButtonWork() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster, showCloseButton = true,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        composeTestRule.onNodeWithTag("CloseButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CloseButton").performClick()
        composeTestRule.onNodeWithTag("CloseButton").assertDoesNotExist()
    }

    @Test
    fun doesActionButtonWork() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show(
            "Hello",
            action = TextToastAction(
                text = "Dismiss",
                onClick = { toaster.dismiss(it) },
            ),
        )
        composeTestRule.onNodeWithText("Dismiss").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dismiss").performClick()
        composeTestRule.onNodeWithText("Dismiss").assertDoesNotExist()
    }

    @Test
    fun swipeDownToDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeDown(startY = top, endY = bottom)
        }
        composeTestRule.onNodeWithTag("Toaster").assertDoesNotExist()
    }

    @Test
    fun swipeUpToDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    swipeDirections = ToastSwipeDirections.Up,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeUp(startY = bottom, endY = top)
        }
        composeTestRule.onNodeWithTag("Toaster").assertDoesNotExist()
    }

    @Test
    fun swipeLeftAndRightToDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    swipeDirections = ToastSwipeDirections.Horizontal,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Swipe Left")
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeLeft(startX = right, endX = left)
        }
        composeTestRule.onNodeWithTag("Toaster").assertDoesNotExist()

        toaster.show("Swipe Right")
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeRight(startX = left, endX = right)
        }
        composeTestRule.onNodeWithTag("Toaster").assertDoesNotExist()
    }

    @Test
    fun swipeInDisabledDirectionDoesNotDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    swipeDirections = ToastSwipeDirections.Down + ToastSwipeDirections.Right,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()

        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeUp(startY = bottom, endY = top)
        }
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()

        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeLeft(startX = right, endX = left)
        }
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()

        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeRight(startX = left, endX = right)
        }
        composeTestRule.onNodeWithTag("Toaster").assertDoesNotExist()
    }

    @Test
    fun swipeDisabledDoesNotDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    swipeable = false,
                    swipeDirections = ToastSwipeDirections.All,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            swipeDown(startY = top, endY = bottom)
        }
        composeTestRule.onNodeWithTag("Toaster").assertIsDisplayed()
    }

    @Test
    fun fastDragThenPauseBeforeReleaseDoesNotDismiss() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        val toastNode = composeTestRule.onNodeWithTag("Toaster")
        toastNode.assertIsDisplayed()
        toastNode.performTouchInput {
            val dragDistance = viewConfiguration.touchSlop + height * 0.35f
            down(center)
            moveBy(Offset(0f, dragDistance / 2f), delayMillis = 16)
            moveBy(Offset(0f, dragDistance / 2f), delayMillis = 16)
            advanceEventTime(250)
            up()
        }
        toastNode.assertIsDisplayed()
    }

    @Test
    fun immediateShortFlingDismisses() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        val toastNode = composeTestRule.onNodeWithTag("Toaster")
        toastNode.assertIsDisplayed()
        toastNode.performTouchInput {
            val dragDistance = viewConfiguration.touchSlop + height * 0.35f
            down(center)
            moveBy(Offset(0f, dragDistance / 2f), delayMillis = 16)
            moveBy(Offset(0f, dragDistance / 2f), delayMillis = 16)
            up()
        }
        toastNode.assertDoesNotExist()
    }

    @Test
    fun swipeDirectionsChangeDuringActiveDragResetsOffset() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        var swipeDirections by mutableStateOf(ToastSwipeDirections.Down)
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    swipeDirections = swipeDirections,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                )
            }
        }

        toaster.show("Hello")
        val textNode = composeTestRule.onNodeWithText("Hello")
        textNode.assertIsDisplayed()
        val initialTop = textNode.getUnclippedBoundsInRoot().top

        composeTestRule.onNodeWithTag("Toaster").performTouchInput {
            down(center)
            moveBy(Offset(0f, 40f), delayMillis = 16)
            moveBy(Offset(0f, 20f), delayMillis = 16)
        }
        composeTestRule.waitForIdle()
        val displacedTop = textNode.getUnclippedBoundsInRoot().top
        assertTrue("Expected toast to be displaced during drag", displacedTop > initialTop)

        swipeDirections = ToastSwipeDirections.None
        composeTestRule.waitForIdle()

        val resetTop = textNode.getUnclippedBoundsInRoot().top
        assertEquals(initialTop.value, resetTop.value, 0.5f)
        textNode.assertIsDisplayed()
    }

    @Test
    fun disabledSwipeDirectionIsNotConsumedByToast() = runTest {
        val toaster = ToasterState(coroutineScope = this)
        var parentHorizontalDrag = 0f
        composeTestRule.setContent {
            Box(modifier = Modifier.size(500.dp)) {
                Toaster(
                    state = toaster,
                    swipeDirections = ToastSwipeDirections.Down + ToastSwipeDirections.Right,
                    enterTransitionDuration = 0,
                    exitTransitionDuration = 0,
                    toastBox = { _, content ->
                        Box(
                            modifier = Modifier.pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    parentHorizontalDrag += dragAmount
                                }
                            },
                        ) {
                            content()
                        }
                    },
                )
            }
        }

        toaster.show("Hello")
        val toastNode = composeTestRule.onNodeWithTag("Toaster")
        toastNode.assertIsDisplayed()
        toastNode.performTouchInput {
            swipeLeft(startX = right, endX = left)
        }
        toastNode.assertIsDisplayed()
        assertTrue(
            "Expected enclosing handler to receive unconsumed left drag, got $parentHorizontalDrag",
            parentHorizontalDrag < 0f,
        )
    }
}

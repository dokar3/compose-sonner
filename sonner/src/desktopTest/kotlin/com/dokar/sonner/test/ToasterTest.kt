package com.dokar.sonner.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import com.dokar.sonner.TextToastAction
import com.dokar.sonner.ToastType
import com.dokar.sonner.Toaster
import com.dokar.sonner.ToasterState
import kotlinx.coroutines.test.runTest
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
}
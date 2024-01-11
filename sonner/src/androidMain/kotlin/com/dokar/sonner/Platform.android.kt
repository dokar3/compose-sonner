package com.dokar.sonner

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup

internal actual fun currentNanoTime(): Long = System.nanoTime()

internal actual val CloseButtonSize: Dp = 24.dp

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("ClickableViewAccessibility")
@Composable
internal actual fun ToasterPopup(
    alignment: Alignment,
    modifier: Modifier,
    offset: IntOffset,
    content: @Composable () -> Unit,
) {
    val backView = LocalView.current

    Popup(alignment = alignment) {
        val innerView = LocalView.current

        var contentBounds by remember { mutableStateOf(Rect.Zero) }

        Box(
            modifier = Modifier
                // This prevents the layout jump when the content size is changed
                // Reason: windowManager.updateViewLayout() will animate the view position
                .fillMaxSize()
                .pointerInteropFilter {
                    val isTouchOnContent = contentBounds.contains(Offset(it.x, it.y))
                    if (isTouchOnContent && it.action == MotionEvent.ACTION_DOWN) {
                        // We are going to handle these touch events
                        return@pointerInteropFilter false
                    }

                    val outerLocation = intArrayOf(0, 0)
                    val innerLocation = intArrayOf(0, 0)
                    backView.getLocationOnScreen(outerLocation)
                    innerView.getLocationOnScreen(innerLocation)
                    val offsetX = innerLocation[0] - outerLocation[0]
                    val offsetY = innerLocation[1] - outerLocation[1]

                    it.offsetLocation(offsetX.toFloat(), offsetY.toFloat())

                    // Send touch events to the back content
                    backView.dispatchTouchEvent(it)
                }
                .offset { offset },
            contentAlignment = alignment,
        ) {
            Box(modifier = Modifier.onGloballyPositioned {
                contentBounds = Rect(offset = it.positionInRoot(), size = it.size.toSize())
            }) {
                content()
            }
        }
    }
}

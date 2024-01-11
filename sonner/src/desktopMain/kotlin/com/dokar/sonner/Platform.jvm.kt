package com.dokar.sonner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

internal actual fun currentNanoTime(): Long = System.nanoTime()

internal actual val CloseButtonSize: Dp = 20.dp

@Composable
internal actual fun ToasterPopup(
    alignment: Alignment,
    modifier: Modifier,
    offset: IntOffset,
    content: @Composable () -> Unit,
) {
    Popup(alignment = alignment, offset = offset) {
        content()
    }
}
package com.dokar.sonner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset

internal expect fun currentNanoTime(): Long

internal expect val CloseButtonSize: Dp

@Composable
internal expect fun ToasterPopup(
    alignment: Alignment,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    content: @Composable () -> Unit,
)
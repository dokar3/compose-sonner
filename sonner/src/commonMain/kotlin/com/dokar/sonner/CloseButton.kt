package com.dokar.sonner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.dokar.sonner.icons.Close

/**
 * The default toast close button.
 */
@Composable
fun ToastCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke = LocalToastBorderStroke.current,
    background: Brush = LocalToastBackground.current,
    tint: Color = LocalToastContentColor.current,
    size: Dp = CloseButtonSize,
    shape: Shape = CircleShape,
    offset: DpOffset = DpOffset(-(8).dp, -(8).dp),
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(size)
            .offset(offset.x, offset.y)
            .clip(CircleShape)
            .background(background)
            .border(
                width = border.width,
                brush = border.brush,
                shape = shape,
            )
            .clickable { onClick() }
            .padding(4.dp)
            .testTag("CloseButton"),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = Close,
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(tint),
        )
    }
}
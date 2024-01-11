package com.dokar.sonner

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

/**
 * Composition local to get the content color within the toast slots.
 */
val LocalToastContentColor = compositionLocalOf { Color.Black }

/**
 * Composition local to get the border stroke within the toast slots.
 */
val LocalToastBorderStroke = compositionLocalOf { BorderStroke(1.dp, Color.LightGray) }

/**
 * Composition local to get the background brush within the toast slots.
 */
val LocalToastBackground: ProvidableCompositionLocal<Brush> = compositionLocalOf {
    SolidColor(Color.White)
}
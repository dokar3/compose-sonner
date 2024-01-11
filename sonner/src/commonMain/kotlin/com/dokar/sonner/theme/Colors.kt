package com.dokar.sonner.theme

import androidx.compose.ui.graphics.Color
import com.dokar.sonner.ToastType

// Colors are converted from:
// https://github.com/emilkowalski/sonner/blob/6a9460389699d6862579e3e4eceeac269bc758d7/src/styles.css

internal fun colorsOf(type: ToastType, darkTheme: Boolean): ToastColors {
    val colors = if (darkTheme) DarkToastColors else LightToastColors
    return colors[type]!!
}

internal val LightToastColors = mapOf(
    ToastType.Normal to ToastColors(
        background = Color.White,
        content = Color(0xff171717),
        border = Color(0xffededed),
    ),
    ToastType.Success to ToastColors(
        background = Color(0xffecfdf3),
        content = Color(0xff008a2e),
        border = Color(0xffd3fde5),
    ),
    ToastType.Info to ToastColors(
        background = Color(0xfff0f8ff),
        content = Color(0xfff0973dc),
        border = Color(0xffd3e0fd),
    ),
    ToastType.Warning to ToastColors(
        background = Color(0xfffffcf0),
        content = Color(0xffdc7609),
        border = Color(0xfffdf5d3),
    ),
    ToastType.Error to ToastColors(
        background = Color(0xfffff0f0),
        content = Color(0xffe60000),
        border = Color(0xffffe0e1),
    ),
)

internal val DarkToastColors = mapOf(
    ToastType.Normal to ToastColors(
        background = Color.Black,
        content = Color(0xfffcfcfc),
        border = Color(0xff333333),
    ),
    ToastType.Success to ToastColors(
        background = Color(0xff001f0f),
        content = Color(0xff59f3a6),
        border = Color(0xff003d1c),
    ),
    ToastType.Info to ToastColors(
        background = Color(0xff000d1f),
        content = Color(0xff5896f3),
        border = Color(0xff00113d),
    ),
    ToastType.Warning to ToastColors(
        background = Color(0xff1d1f00),
        content = Color(0xfff3cf58),
        border = Color(0xff3d3d00),
    ),
    ToastType.Error to ToastColors(
        background = Color(0xff2d0607),
        content = Color(0xffff9ea1),
        border = Color(0xff4d0408),
    ),
)

internal class ToastColors(
    val background: Color,
    val content: Color,
    val border: Color,
)

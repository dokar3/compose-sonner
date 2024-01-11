package com.dokar.sonner.sample

import androidx.compose.runtime.Composable

fun interface LinkOpener {
    fun open(url: String)
}

@Composable
expect fun rememberLinkOpener(): LinkOpener
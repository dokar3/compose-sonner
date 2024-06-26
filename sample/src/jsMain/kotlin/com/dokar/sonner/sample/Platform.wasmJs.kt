package com.dokar.sonner.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

@Composable
actual fun rememberLinkOpener(): LinkOpener {
    return remember {
        LinkOpener {
            window.open(url = it, target = "_blank")
        }
    }
}
package com.dokar.sonner.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberLinkOpener(): LinkOpener {
    return remember {
        LinkOpener {
            openInBrowser(it)
        }
    }
}

private fun openInBrowser(url: String?) {
    val nsUrl = url?.let { NSURL.URLWithString(it) } ?: return
    UIApplication.sharedApplication.openURL(nsUrl)
}

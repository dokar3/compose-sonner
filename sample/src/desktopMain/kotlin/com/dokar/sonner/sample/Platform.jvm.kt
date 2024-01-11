package com.dokar.sonner.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.Desktop
import java.net.URI
import java.util.Locale

@Composable
actual fun rememberLinkOpener(): LinkOpener {
    return remember {
        LinkOpener {
            openInBrowser(URI.create(it))
        }
    }
}

// https://stackoverflow.com/a/68426773
private fun openInBrowser(uri: URI) {
    val osName by lazy(LazyThreadSafetyMode.NONE) {
        System.getProperty("os.name").lowercase(Locale.getDefault())
    }
    val desktop = Desktop.getDesktop()
    when {
        Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE) ->
            desktop.browse(uri)

        "mac" in osName -> Runtime.getRuntime().exec("open $uri")
        "nix" in osName || "nux" in osName -> Runtime.getRuntime().exec("xdg-open $uri")
        else -> {}
    }
}

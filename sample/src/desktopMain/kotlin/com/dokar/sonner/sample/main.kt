package com.dokar.sonner.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "compose-sonner") {
        SampleScreen()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    SampleScreen()
}
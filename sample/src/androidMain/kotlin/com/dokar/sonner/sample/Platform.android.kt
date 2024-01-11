package com.dokar.sonner.sample

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberLinkOpener(): LinkOpener {
    val context = LocalContext.current
    return remember(context) {
        LinkOpener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(it))
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
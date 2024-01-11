package com.dokar.sonner

import androidx.compose.ui.Alignment

/**
 * Only builtin alignments are supported.
 */
internal fun Alignment.isBottomAlign(): Boolean {
    return this == Alignment.BottomCenter ||
            this == Alignment.BottomStart ||
            this == Alignment.BottomEnd
}
package com.dokar.sonner

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/**
 * Helper class to calculate toast item transforms.
 */
@Stable
internal class ToastTransformHelper(
    private val density: Density,
    private val maxVisibleToasts: Int,
) {
    fun calcScale(layoutIndex: Int): Float {
        val index = layoutIndex.coerceIn(0, maxVisibleToasts)
        return 1f - 0.4f * (index / maxVisibleToasts.toFloat())
    }

    fun calcTranslationY(
        itemHeightProvider: ItemHeightProvider,
        isBottomAlign: Boolean,
        expanded: Boolean,
        layoutIndex: Int,
    ): Float {
        val index = layoutIndex.coerceIn(0, maxVisibleToasts)
        if (index == 0) {
            // No offset for the front item
            return 0f
        }
        val frontItemHeight = itemHeightProvider.get(0)
        val factor = if (isBottomAlign) -1f else 1f
        return if (expanded) {
            val impactOffset = with(density) { ((-16).dp).toPx() }
            var tranY = 0f
            // Just add up the heights of the items
            for (i in 0..<index) {
                tranY += itemHeightProvider.get(0) + impactOffset
            }
            tranY * factor
        } else {
            val maxOffset = with(density) { 12.dp.toPx() }
            val scale = 1f - 0.3f * (index / maxVisibleToasts.toFloat())
            val diff = if (frontItemHeight > 0) {
                // Apply the diff between the current item height and the front item height,
                // or else the current item will get overlapped if they have different heights.
                frontItemHeight - itemHeightProvider.get(index)
            } else {
                0
            }
            (maxOffset * index + diff) * scale * factor
        }
    }
}
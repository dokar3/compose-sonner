package com.dokar.sonner

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull

@Stable
internal class ItemHeightProvider {
    private val itemHeights = mutableStateOf<Map<Int, Int>>(emptyMap())

    fun updateItemHeights(heights: Map<Int, Int>) {
        itemHeights.value = heights
    }

    fun get(layoutIndex: Int): Int {
        return itemHeights.value[layoutIndex] ?: 0
    }

    fun listen(layoutIndex: Int): Flow<Int> {
        return snapshotFlow { itemHeights.value }
            .mapNotNull { itemHeights.value[layoutIndex] }
            .distinctUntilChanged()
    }
}
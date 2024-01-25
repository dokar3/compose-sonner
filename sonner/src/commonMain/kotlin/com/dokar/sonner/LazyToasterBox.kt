package com.dokar.sonner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun rememberLazyToasterBoxState(
    maxVisibleToasts: Int,
    itemCountProvider: () -> Int,
    key: (index: Int) -> Any,
    indexOfKey: (key: Any) -> Int,
    isItemDismissed: (index: Int) -> Boolean,
): LazyToasterBoxState {
    return remember {
        LazyToasterBoxState(
            maxVisibleToasts = maxVisibleToasts,
            itemCountProvider = itemCountProvider,
            key = key,
            indexOfKey = indexOfKey,
            isItemDismissed = isItemDismissed,
        )
    }.also {
        it.maxVisibleToasts = maxVisibleToasts
    }
}

/**
 * A lazy layout that stacks all children at the top or bottom side according to the [alignment].
 *
 * The [alignment] does not really to be used, only for determining the top/bottom.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyToasterBox(
    state: LazyToasterBoxState,
    expanded: Boolean,
    itemHeightProvider: ItemHeightProvider,
    toastTransformHelper: ToastTransformHelper,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.BottomCenter,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    itemContent: @Composable (index: Int) -> Unit,
) {
    val itemProvider = remember(state) {
        ToasterItemProvider(
            state = state,
            itemContent = itemContent,
        )
    }

    LazyLayout(
        itemProvider = { itemProvider },
        modifier = modifier,
    ) { constraints ->
        val topPadding = contentPadding.calculateTopPadding().roundToPx()
        val bottomPadding = contentPadding.calculateBottomPadding().roundToPx()
        val startPadding = contentPadding.calculateStartPadding(layoutDirection).roundToPx()
        val endPadding = contentPadding.calculateEndPadding(layoutDirection).roundToPx()

        val updatedConstrains = constraints.copy(
            maxHeight = constraints.maxHeight - topPadding - bottomPadding,
            maxWidth = constraints.maxWidth - startPadding - endPadding,
        )

        val visibleItemIndices = state.visibleItemIndices()
        if (visibleItemIndices.isEmpty()) {
            return@LazyLayout layout(startPadding + endPadding, topPadding + bottomPadding) {}
        }

        val placeables = visibleItemIndices.flatMap {
            if (!state.isItemDismissed(it)) {
                measure(it, updatedConstrains)
            } else {
                emptyList()
            }
        }
        if (placeables.isEmpty()) {
            return@LazyLayout layout(startPadding + endPadding, topPadding + bottomPadding) {}
        }

        var maxItemWidth = 0
        var totalItemHeight = 0
        val layoutHeightMap = mutableMapOf<Int, Int>()
        for (i in placeables.indices) {
            val placeable = placeables[i]
            maxItemWidth = max(maxItemWidth, placeable.width)
            totalItemHeight += placeable.height
            val layoutIndex = placeables.lastIndex - i
            layoutHeightMap[layoutIndex] = placeable.height
        }

        val layoutWidth = maxItemWidth + startPadding + endPadding

        val stackFromBottom = alignment.isBottomAlign()

        itemHeightProvider.updateItemHeights(layoutHeightMap)

        val lastItemTranY = toastTransformHelper.calcTranslationY(
            itemHeightProvider = itemHeightProvider,
            isBottomAlign = stackFromBottom,
            expanded = expanded,
            layoutIndex = placeables.lastIndex,
        )
        val layoutHeight = min(
            constraints.maxHeight,
            placeables.last().height + lastItemTranY.absoluteValue.toInt(),
        )

        layout(layoutWidth, layoutHeight) {
            for (i in placeables.indices) {
                val placeable = placeables[i]
                val y = if (stackFromBottom) {
                    layoutHeight - placeable.height - bottomPadding
                } else {
                    topPadding
                }
                placeable.place(
                    x = startPadding,
                    y = y
                )
            }
        }
    }
}

@ExperimentalFoundationApi
private class ToasterItemProvider(
    private val state: LazyToasterBoxState,
    private val itemContent: @Composable (index: Int) -> Unit,
) : LazyLayoutItemProvider {
    override val itemCount: Int get() = state.itemCount()

    @Composable
    override fun Item(index: Int, key: Any) {
        itemContent(index)
    }

    override fun getKey(index: Int): Any {
        return state.keyForIndex(index)
    }

    override fun getContentType(index: Int): Any {
        return 0
    }

    override fun getIndex(key: Any): Int {
        return state.indexOfKey(0)
    }
}

@Stable
internal class LazyToasterBoxState(
    maxVisibleToasts: Int,
    private val itemCountProvider: () -> Int,
    private val key: (index: Int) -> Any,
    private val indexOfKey: (key: Any) -> Int,
    private val isItemDismissed: (index: Int) -> Boolean,
) {
    var maxVisibleToasts by mutableIntStateOf(maxVisibleToasts)

    fun itemCount() = itemCountProvider()

    fun visibleItemIndices(): List<Int> {
        val maxCount = maxVisibleToasts + 1
        val visibleIndices = mutableListOf<Int>()
        val lastIndex = itemCount() - 1
        for (i in lastIndex downTo 0) {
            if (visibleIndices.size == maxCount) break
            if (!isItemDismissed(i)) {
                visibleIndices.add(i)
            }
        }
        return visibleIndices.reversed()
    }

    fun keyForIndex(index: Int) = key(index)

    fun indexOfKey(key: Any) = indexOfKey.invoke(key)

    fun isItemDismissed(index: Int): Boolean {
        return isItemDismissed.invoke(index)
    }
}

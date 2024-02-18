package com.dokar.sonner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.max

/**
 * The toaster, within a [Popup]. It will display toasts from the toaster [state].
 *
 * @param state The state of the toaster, managing the toasts to be displayed.
 * @param modifier The modifier to be applied to the container.
 * @param maxVisibleToasts Maximum number of toasts visible at the same time.
 * @param expanded Whether toasts are expanded vertically instead of stacked.
 * @param swipeable Whether toasts can be dismissed with a swipe down gesture.
 * @param richColors Whether to use rich colors for toasts that have a different type than [ToastType.Normal].
 * @param darkTheme Whether toasts are in dark theme.
 * @param showCloseButton Whether to show a close button on the top-left corner for each toast.
 * @param contentColor Composable function that provides the content color of each toast.
 * @param border Composable function that provides the border stroke of each toast.
 * @param background Composable function that provides the background brush of each toast.
 * @param shape Shape of toasts.
 * @param elevation `elevation` of toast's [shadow] modifier.
 * @param shadowAmbientColor `ambientColor` of toast's [shadow] modifier.
 * @param shadowSpotColor `spotColor` of toast's [shadow] modifier.
 * @param containerPadding Padding for the toaster container.
 * @param contentPadding Padding for the content of each toast.
 * @param widthPolicy The width policy for each toast.
 * @param alignment Alignment of the toaster within the popup.
 * @param offset Offset of the popup.
 * @param enterTransitionDuration Duration of the enter transition for each toast.
 * @param exitTransitionDuration Duration of the exit transition for each toast.
 * @param iconSlot Composable slot for the toast icon.
 * @param messageSlot Composable slot for the toast message.
 * @param actionSlot Composable slot for the toast action.
 * @param closeButton The close button, if set, [showCloseButton] will be ignored.
 * @param toastBox A wrapper for each toast.
 */
@Composable
fun Toaster(
    state: ToasterState,
    modifier: Modifier = Modifier,
    maxVisibleToasts: Int = 3,
    expanded: Boolean = false,
    swipeable: Boolean = true,
    richColors: Boolean = false,
    darkTheme: Boolean = false,
    showCloseButton: Boolean = false,
    contentColor: @Composable (toast: Toast) -> Color = {
        ToasterDefaults.contentColor(it, richColors, darkTheme)
    },
    border: @Composable (toast: Toast) -> BorderStroke = {
        ToasterDefaults.border(it, richColors, darkTheme)
    },
    background: @Composable (toast: Toast) -> Brush = {
        ToasterDefaults.background(it, richColors, darkTheme)
    },
    shape: @Composable (toast: Toast) -> Shape = { ToasterDefaults.Shape },
    elevation: Dp = ToasterDefaults.Elevation,
    shadowAmbientColor: Color = if (darkTheme) {
        ToasterDefaults.DarkShadowAmbientColor
    } else {
        ToasterDefaults.ShadowAmbientColor
    },
    shadowSpotColor: Color = if (darkTheme) {
        ToasterDefaults.DarkShadowSpotColor
    } else {
        ToasterDefaults.ShadowSpotColor
    },
    containerPadding: PaddingValues = PaddingValues(0.dp),
    contentPadding: @Composable (toast: Toast) -> PaddingValues = { PaddingValues(16.dp) },
    widthPolicy: @Composable (toast: Toast) -> ToastWidthPolicy = { ToastWidthPolicy() },
    alignment: Alignment = Alignment.BottomCenter,
    offset: IntOffset = IntOffset.Zero,
    enterTransitionDuration: Int = ToasterDefaults.ENTER_TRANSITION_DURATION,
    exitTransitionDuration: Int = ToasterDefaults.EXIT_TRANSITION_DURATION,
    dismissPause: ToastDismissPause = ToastDismissPause.OnInvisible,
    iconSlot: @Composable (toast: Toast) -> Unit = { ToasterDefaults.iconSlot(it) },
    messageSlot: @Composable (toast: Toast) -> Unit = { ToasterDefaults.messageSlot(it) },
    actionSlot: @Composable (toast: Toast) -> Unit = { ToasterDefaults.actionSlot(it) },
    closeButton: @Composable (BoxScope.(toast: Toast) -> Unit)? = if (showCloseButton) {
        {
            ToastCloseButton(onClick = { state.dismiss(it.id) })
        }
    } else null,
    toastBox: @Composable (toast: Toast, toastContent: @Composable () -> Unit) -> Unit =
        { _, content -> content() }
) {
    require(maxVisibleToasts > 0) { "maxVisibleToasts should be at least 1." }

    if (state.toasts.isEmpty()) return

    ToasterPopup(alignment = alignment, offset = offset) {
        val density = LocalDensity.current

        val lazyToasterBoxState = rememberLazyToasterBoxState(
            maxVisibleToasts = maxVisibleToasts,
            itemCountProvider = { state.toasts.size },
            key = { index -> state.toasts[index].toast.id },
            indexOfKey = { key -> state.toasts.indexOfFirst { it.toast.id == key } },
            isItemDismissed = { index -> state.toasts[index].isDismissed }
        )


        val itemHeightProvider = remember { ItemHeightProvider() }

        val toastTransformHelper = remember(density, maxVisibleToasts) {
            ToastTransformHelper(density = density, maxVisibleToasts = maxVisibleToasts)
        }

        LaunchedEffect(state.toasts) {
            state.dismissingToastsFlow()
                .collect { item ->
                    val visibleItemIndices = lazyToasterBoxState.visibleItemIndices()
                    val index = state.toasts.indexOf(item)
                    if (index !in visibleItemIndices) {
                        // Item dismissed but not currently visible, mark it as
                        // dismissed state and don't render it on UI
                        state.markDismissed(item.toast.id)
                    }
                }
        }

        ApplyToastDismissPause(
            state = state,
            toastDismissPause = dismissPause,
            lazyToasterBoxState = lazyToasterBoxState,
            maxVisibleToasts = maxVisibleToasts,
        )

        LazyToasterBox(
            state = lazyToasterBoxState,
            expanded = expanded,
            itemHeightProvider = itemHeightProvider,
            toastTransformHelper = toastTransformHelper,
            contentPadding = containerPadding,
            alignment = alignment,
            modifier = modifier.testTag("Toaster"),
        ) { index ->
            val item = state.toasts[index]
            val toast = item.toast

            var invisibleItemCount by remember { mutableIntStateOf(0) }

            LaunchedEffect(state.toasts.lastIndex, index) {
                // This finds all dismissing or dismissed item count above this current item,
                // will make our toasts animated after some have dismissed.
                state.invisibleItemsInRangeFlow(
                    start = index + 1,
                    end = state.toasts.lastIndex
                )
                    .collect { invisibleItemCount = it }
            }

            val layoutIndex = state.toasts.lastIndex - index - invisibleItemCount

            val currentBorder = border(toast)
            val currentBackground = background(toast)
            val currentContentColor = contentColor(toast)

            CompositionLocalProvider(
                LocalToastBorderStroke provides currentBorder,
                LocalToastBackground provides currentBackground,
                LocalToastContentColor provides currentContentColor,
            ) {
                toastBox(item.toast) {
                    ToastItem(
                        onRequestDismiss = { state.dismiss(toast.id) },
                        onInvisible = { state.markDismissed(toast.id) },
                        expanded = expanded,
                        layoutIndex = layoutIndex,
                        toast = item.toast,
                        dismissing = item.isDismissing,
                        maxVisibleToasts = maxVisibleToasts,
                        widthPolicy = widthPolicy(toast),
                        swipeable = swipeable,
                        elevation = elevation,
                        shadowAmbientColor = shadowAmbientColor,
                        shadowSpotColor = shadowSpotColor,
                        shape = shape(toast),
                        contentPadding = contentPadding(toast),
                        alignment = alignment,
                        enterTransitionDuration = enterTransitionDuration,
                        exitTransitionDuration = exitTransitionDuration,
                        transformHelper = toastTransformHelper,
                        itemHeightProvider = itemHeightProvider,
                        modifier = Modifier,
                        border = currentBorder,
                        background = currentBackground,
                        iconSlot = iconSlot,
                        messageSlot = messageSlot,
                        actionSlot = actionSlot,
                        closeButton = closeButton,
                    )
                }
            }
        }
    }
}

@Composable
private inline fun ApplyToastDismissPause(
    state: ToasterState,
    toastDismissPause: ToastDismissPause,
    lazyToasterBoxState: LazyToasterBoxState,
    maxVisibleToasts: Int,
) {
    LaunchedEffect(state, lazyToasterBoxState, toastDismissPause) {
        snapshotFlow { state.toasts.map { arrayOf(it.toast, it.state) } }
            .map { lazyToasterBoxState.visibleItemIndices() }
            .collect { visibleIndices ->
                val toasts = state.toasts
                if (toasts.isEmpty() || visibleIndices.isEmpty()) return@collect
                when (toastDismissPause) {
                    ToastDismissPause.Never -> {
                        for (toast in toasts) {
                            state.resumeDismissTimer(toast.toast.id)
                        }
                    }

                    ToastDismissPause.OnNotFront -> {
                        // Resume the dismiss timer for the front toast
                        val frontToastIndex = visibleIndices.last()
                        state.resumeDismissTimer(toasts[frontToastIndex].toast.id)
                        // Pause others
                        for (i in toasts.indices) {
                            if (i != frontToastIndex) {
                                state.pauseDismissTimer(toasts[i].toast.id)
                            }
                        }
                    }

                    ToastDismissPause.OnInvisible -> {
                        val realVisibleIndices = if (visibleIndices.size > maxVisibleToasts) {
                            // Exclude items that are marked as visible but are not,
                            // for the animation reason
                            val from = visibleIndices.size - maxVisibleToasts
                            val to = visibleIndices.size
                            visibleIndices.subList(from, to)
                        } else {
                            visibleIndices
                        }
                        // Resume dismiss timer for visible toasts
                        for (index in realVisibleIndices) {
                            state.resumeDismissTimer(toasts[index].toast.id)
                        }
                        // Pause others
                        val visibleIndexSet = realVisibleIndices.toSet()
                        for (i in toasts.indices) {
                            if (!visibleIndexSet.contains(i)) {
                                state.pauseDismissTimer(toasts[i].toast.id)
                            }
                        }
                    }
                }
            }
    }
}

/**
 * The toast item.
 *
 * @param layoutIndex Starts from the most front item and starts from 0.
 */
@Composable
private fun ToastItem(
    onRequestDismiss: () -> Unit,
    onInvisible: () -> Unit,
    expanded: Boolean,
    layoutIndex: Int,
    toast: Toast,
    dismissing: Boolean,
    maxVisibleToasts: Int,
    widthPolicy: ToastWidthPolicy,
    swipeable: Boolean,
    elevation: Dp,
    shadowAmbientColor: Color,
    shadowSpotColor: Color,
    shape: Shape,
    contentPadding: PaddingValues,
    alignment: Alignment,
    enterTransitionDuration: Int,
    exitTransitionDuration: Int,
    transformHelper: ToastTransformHelper,
    itemHeightProvider: ItemHeightProvider,
    modifier: Modifier = Modifier,
    border: BorderStroke,
    background: Brush,
    iconSlot: @Composable (toast: Toast) -> Unit,
    messageSlot: @Composable (toast: Toast) -> Unit,
    actionSlot: @Composable (toast: Toast) -> Unit,
    closeButton: @Composable (BoxScope.(toast: Toast) -> Unit)?,
) {
    val visibleState = remember {
        MutableTransitionState(false).also { it.targetState = true }
    }

    var frontItemHeight by remember { mutableIntStateOf(itemHeightProvider.get(0)) }

    var height by remember { mutableIntStateOf(0) }

    var dragY by remember { mutableFloatStateOf(0f) }

    val isBottomAlign = alignment.isBottomAlign()

    val draggableState = rememberDraggableState(
        onDelta = { delta -> dragY = max(0f, dragY + delta) }
    )

    fun isSwipedToDismiss(velocity: Float): Boolean {
        if (velocity > 600f && dragY >= height / 5f) return true
        if (velocity > 300f && dragY >= height / 3f) return true
        if (velocity > 100f && dragY >= height / 2f) return true
        return dragY > height * 0.8f
    }

    val scale = animateFloatAsState(
        targetValue = if (!expanded) transformHelper.calcScale(layoutIndex) else 1f,
        animationSpec = tween(durationMillis = exitTransitionDuration),
    )
    val alpha = animateFloatAsState(
        targetValue = if (layoutIndex < maxVisibleToasts) 1f else 0f,
        animationSpec = tween(durationMillis = exitTransitionDuration),
    )
    val tranY = animateFloatAsState(
        targetValue = transformHelper.calcTranslationY(
            itemHeightProvider = itemHeightProvider,
            isBottomAlign = isBottomAlign,
            expanded = expanded,
            layoutIndex = layoutIndex,
        ),
        animationSpec = tween(durationMillis = exitTransitionDuration),
    )

    LaunchedEffect(visibleState, dismissing) {
        if (dismissing) {
            visibleState.targetState = false
        }
    }

    LaunchedEffect(visibleState, onInvisible) {
        // Remove toast after finishing the dismiss transition
        snapshotFlow { visibleState.isIdle && !visibleState.targetState }
            .filter { invisible -> invisible }
            .collect { onInvisible() }
    }

    LaunchedEffect(itemHeightProvider) {
        // Listen to height changes of most front item, so we can adjust our
        // item offset automatically when a new item is added.
        itemHeightProvider.listen(0)
            .collect { frontItemHeight = it }
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInVertically(tween(durationMillis = enterTransitionDuration)) {
            if (isBottomAlign) it else -it
        } + fadeIn(tween(durationMillis = enterTransitionDuration)),
        exit = slideOutVertically(tween(durationMillis = exitTransitionDuration)) {
            if (isBottomAlign) it else -it
        } + fadeOut(tween(durationMillis = exitTransitionDuration)),
        modifier = modifier
            .onSizeChanged { height = it.height }
            .graphicsLayer {
                this.alpha = alpha.value
                transformOrigin = TransformOrigin(0.5f, if (isBottomAlign) 0f else 1f)
                scaleX = scale.value
                scaleY = scale.value
                translationY = tranY.value + dragY
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            )
            .draggable(
                state = draggableState,
                enabled = swipeable,
                orientation = Orientation.Vertical,
                onDragStarted = { dragY = 0f },
                onDragStopped = { velocity ->
                    if (!isSwipedToDismiss(velocity)) {
                        animate(
                            targetValue = 0f,
                            initialValue = dragY,
                            animationSpec = tween(durationMillis = exitTransitionDuration),
                        ) { value, _ ->
                            dragY = value
                        }
                    } else {
                        onRequestDismiss()
                    }
                },
            ),
    ) {
        Box(modifier = Modifier.padding(max(elevation * 1.5f, 10.dp))) {
            Row(
                modifier = Modifier
                    .widthIn(min = widthPolicy.min, max = widthPolicy.max)
                    .let { if (widthPolicy.fillMaxWidth) it.fillMaxWidth() else it }
                    .shadow(
                        elevation = elevation,
                        shape = shape,
                        ambientColor = shadowAmbientColor,
                        spotColor = shadowSpotColor,
                    )
                    .border(
                        width = border.width,
                        brush = border.brush,
                        shape = shape,
                    )
                    .background(
                        brush = background,
                        shape = shape,
                    )
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    iconSlot(toast)
                    messageSlot(toast)
                }
                actionSlot(toast)
            }

            if (closeButton != null) {
                closeButton(toast)
            }
        }
    }
}

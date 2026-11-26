package com.dokar.sonner

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlin.math.abs

internal fun Modifier.toastSwipeToDismiss(
    swipeDirections: ToastSwipeDirections,
    onDragStart: () -> Unit,
    onDrag: (orientation: Orientation, delta: Float) -> Unit,
    onDragEnd: (orientation: Orientation, velocity: Float) -> Unit,
    onDragCancel: () -> Unit,
): Modifier {
    return pointerInput(swipeDirections) {
        if (swipeDirections.isEmpty) return@pointerInput
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val velocityTracker = VelocityTracker()
            var totalDrag = Offset.Zero
            velocityTracker.addPosition(down.uptimeMillis, totalDrag)

            var lockedOrientation: Orientation? = null
            var initialDelta = 0f

            val dragStart: PointerInputChange? = when {
                swipeDirections.hasVertical && !swipeDirections.hasHorizontal -> {
                    awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                        val dy = if (over != 0f) over else change.position.y - down.position.y
                        val orientation = resolveSwipeOrientation(
                            dx = 0f,
                            dy = dy,
                            swipeDirections = swipeDirections,
                        )
                        if (orientation != null) {
                            totalDrag += Offset(0f, over)
                            velocityTracker.addPosition(change.uptimeMillis, totalDrag)
                            change.consume()
                            lockedOrientation = orientation
                            initialDelta = over
                        }
                    }
                }

                swipeDirections.hasHorizontal && !swipeDirections.hasVertical -> {
                    awaitHorizontalTouchSlopOrCancellation(down.id) { change, over ->
                        val dx = if (over != 0f) over else change.position.x - down.position.x
                        val orientation = resolveSwipeOrientation(
                            dx = dx,
                            dy = 0f,
                            swipeDirections = swipeDirections,
                        )
                        if (orientation != null) {
                            totalDrag += Offset(over, 0f)
                            velocityTracker.addPosition(change.uptimeMillis, totalDrag)
                            change.consume()
                            lockedOrientation = orientation
                            initialDelta = over
                        }
                    }
                }

                else -> {
                    awaitTouchSlopOrCancellation(down.id) { change, over ->
                        val dir = if (over != Offset.Zero) over else change.position - down.position
                        val orientation = resolveSwipeOrientation(
                            dx = dir.x,
                            dy = dir.y,
                            swipeDirections = swipeDirections,
                        )
                        if (orientation != null) {
                            totalDrag += over
                            velocityTracker.addPosition(change.uptimeMillis, totalDrag)
                            change.consume()
                            lockedOrientation = orientation
                            initialDelta = if (orientation == Orientation.Horizontal) over.x else over.y
                        }
                    }
                }
            }

            val orientation = lockedOrientation
            if (dragStart == null || orientation == null) {
                return@awaitEachGesture
            }

            onDragStart()
            var dragFinished = false
            try {
                onDrag(orientation, initialDelta)

                val completed = awaitAxisDrag(
                    pointerId = dragStart.id,
                    orientation = orientation,
                    velocityTracker = velocityTracker,
                    onDrag = { delta -> onDrag(orientation, delta) },
                    addDragDelta = { deltaOffset ->
                        totalDrag += deltaOffset
                        totalDrag
                    },
                )

                dragFinished = true
                if (completed) {
                    val velocity = velocityTracker.calculateVelocity()
                    val axisVelocity = if (orientation == Orientation.Horizontal) {
                        velocity.x
                    } else {
                        velocity.y
                    }
                    onDragEnd(orientation, axisVelocity)
                } else {
                    onDragCancel()
                }
            } finally {
                if (!dragFinished) {
                    onDragCancel()
                }
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitAxisDrag(
    pointerId: PointerId,
    orientation: Orientation,
    velocityTracker: VelocityTracker,
    onDrag: (delta: Float) -> Unit,
    addDragDelta: (Offset) -> Offset,
): Boolean {
    var pointer = pointerId
    while (true) {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull { it.id == pointer } ?: return false
        if (change.changedToUpIgnoreConsumed()) {
            val otherDown = event.changes.firstOrNull { it.pressed }
            if (otherDown == null) {
                velocityTracker.addPosition(change.uptimeMillis, addDragDelta(Offset.Zero))
                return true
            } else {
                pointer = otherDown.id
            }
        } else if (change.isConsumed) {
            return false
        } else {
            val positionChange = change.positionChangeIgnoreConsumed()
            val delta = if (orientation == Orientation.Horizontal) {
                positionChange.x
            } else {
                positionChange.y
            }
            val totalDrag = addDragDelta(
                if (orientation == Orientation.Horizontal) Offset(delta, 0f) else Offset(0f, delta)
            )
            velocityTracker.addPosition(change.uptimeMillis, totalDrag)
            if (delta != 0f) {
                change.consume()
                onDrag(delta)
            }
        }
    }
}

internal fun resolveSwipeOrientation(
    dx: Float,
    dy: Float,
    swipeDirections: ToastSwipeDirections,
): Orientation? {
    if (swipeDirections.isEmpty) return null

    val horizontalAllowed = (dx > 0f && ToastSwipeDirections.Right in swipeDirections) ||
        (dx < 0f && ToastSwipeDirections.Left in swipeDirections)
    val verticalAllowed = (dy > 0f && ToastSwipeDirections.Down in swipeDirections) ||
        (dy < 0f && ToastSwipeDirections.Up in swipeDirections)

    if (swipeDirections.hasVertical && !swipeDirections.hasHorizontal) {
        return if (verticalAllowed) Orientation.Vertical else null
    }
    if (swipeDirections.hasHorizontal && !swipeDirections.hasVertical) {
        return if (horizontalAllowed) Orientation.Horizontal else null
    }

    return when {
        abs(dx) >= abs(dy) && horizontalAllowed -> Orientation.Horizontal
        verticalAllowed -> Orientation.Vertical
        horizontalAllowed -> Orientation.Horizontal
        else -> null
    }
}

internal fun clampDragOffset(
    current: Float,
    delta: Float,
    orientation: Orientation,
    swipeDirections: ToastSwipeDirections,
): Float {
    val next = current + delta
    return when (orientation) {
        Orientation.Horizontal -> {
            val min = if (ToastSwipeDirections.Left in swipeDirections) -Float.MAX_VALUE else 0f
            val max = if (ToastSwipeDirections.Right in swipeDirections) Float.MAX_VALUE else 0f
            next.coerceIn(min, max)
        }

        Orientation.Vertical -> {
            val min = if (ToastSwipeDirections.Up in swipeDirections) -Float.MAX_VALUE else 0f
            val max = if (ToastSwipeDirections.Down in swipeDirections) Float.MAX_VALUE else 0f
            next.coerceIn(min, max)
        }
    }
}

internal fun resolveDismissDirection(
    orientation: Orientation,
    dragOffset: Float,
    velocity: Float,
    width: Int,
    height: Int,
    swipeDirections: ToastSwipeDirections,
): ToastSwipeDirections {
    val direction = when (orientation) {
        Orientation.Horizontal -> when {
            dragOffset > 0f -> ToastSwipeDirections.Right
            dragOffset < 0f -> ToastSwipeDirections.Left
            else -> ToastSwipeDirections.None
        }

        Orientation.Vertical -> when {
            dragOffset > 0f -> ToastSwipeDirections.Down
            dragOffset < 0f -> ToastSwipeDirections.Up
            else -> ToastSwipeDirections.None
        }
    }
    if (direction.isEmpty || direction !in swipeDirections) {
        return ToastSwipeDirections.None
    }

    val distance = abs(dragOffset)
    val directedVelocity = when (direction) {
        ToastSwipeDirections.Right, ToastSwipeDirections.Down -> velocity
        ToastSwipeDirections.Left, ToastSwipeDirections.Up -> -velocity
        else -> 0f
    }

    val dismissed = when (orientation) {
        Orientation.Vertical -> {
            if (height <= 0) {
                false
            } else {
                (directedVelocity > 600f && distance >= height / 5f) ||
                    (directedVelocity > 300f && distance >= height / 3f) ||
                    (directedVelocity > 100f && distance >= height / 2f) ||
                    distance > height * 0.8f
            }
        }

        Orientation.Horizontal -> {
            if (width <= 0) {
                false
            } else {
                (directedVelocity > 600f && distance >= width * 0.15f) ||
                    (directedVelocity > 300f && distance >= width * 0.25f) ||
                    (directedVelocity > 100f && distance >= width * 0.35f) ||
                    distance > width * 0.5f
            }
        }
    }

    return if (dismissed) direction else ToastSwipeDirections.None
}

package com.dokar.sonner

import androidx.compose.runtime.Immutable
import kotlin.jvm.JvmInline

/**
 * Represents a combination of allowed swipe directions to dismiss toasts.
 *
 * Directions can be combined with [plus] and removed with [minus], for example:
 * ```kotlin
 * val directions = ToastSwipeDirections.Down + ToastSwipeDirections.Horizontal
 * ```
 */
@Immutable
@JvmInline
value class ToastSwipeDirections private constructor(internal val mask: Int) {
    /**
     * Combines these swipe directions with [other].
     */
    operator fun plus(other: ToastSwipeDirections): ToastSwipeDirections =
        ToastSwipeDirections(mask or other.mask)

    /**
     * Removes [other] from these swipe directions.
     */
    operator fun minus(other: ToastSwipeDirections): ToastSwipeDirections =
        ToastSwipeDirections(mask and other.mask.inv())

    /**
     * Returns `true` if all directions in [other] are included in these swipe directions.
     */
    operator fun contains(other: ToastSwipeDirections): Boolean =
        other.mask != 0 && (mask and other.mask) == other.mask

    /**
     * Returns `true` if no swipe directions are enabled.
     */
    val isEmpty: Boolean
        get() = mask == 0

    internal val hasHorizontal: Boolean
        get() = (mask and Horizontal.mask) != 0

    internal val hasVertical: Boolean
        get() = (mask and Vertical.mask) != 0

    override fun toString(): String {
        if (isEmpty) return "ToastSwipeDirections.None"
        val parts = buildList(4) {
            if (Up in this@ToastSwipeDirections) add("Up")
            if (Down in this@ToastSwipeDirections) add("Down")
            if (Left in this@ToastSwipeDirections) add("Left")
            if (Right in this@ToastSwipeDirections) add("Right")
        }
        return "ToastSwipeDirections(${parts.joinToString(", ")})"
    }

    companion object {
        val None = ToastSwipeDirections(0)
        val Up = ToastSwipeDirections(1 shl 0)
        val Down = ToastSwipeDirections(1 shl 1)
        val Left = ToastSwipeDirections(1 shl 2)
        val Right = ToastSwipeDirections(1 shl 3)

        val Vertical = Up + Down
        val Horizontal = Left + Right
        val All = Vertical + Horizontal
    }
}

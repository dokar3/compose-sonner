package com.dokar.sonner.test

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.Alignment
import com.dokar.sonner.ToastSwipeDirections
import com.dokar.sonner.ToasterDefaults
import com.dokar.sonner.clampDragOffset
import com.dokar.sonner.resolveDismissDirection
import com.dokar.sonner.resolveSwipeOrientation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ToastSwipeDirectionsTest {
    @Test
    fun bitmaskOperations() {
        assertTrue(ToastSwipeDirections.None.isEmpty)
        assertFalse(ToastSwipeDirections.Down.isEmpty)
        assertFalse(ToastSwipeDirections.None in ToastSwipeDirections.All)

        val combined = ToastSwipeDirections.Down + ToastSwipeDirections.Left
        assertTrue(ToastSwipeDirections.Down in combined)
        assertTrue(ToastSwipeDirections.Left in combined)
        assertFalse(ToastSwipeDirections.Up in combined)
        assertFalse(ToastSwipeDirections.Right in combined)
        assertTrue(combined in ToastSwipeDirections.All)

        val removed = combined - ToastSwipeDirections.Left
        assertEquals(ToastSwipeDirections.Down, removed)
        assertEquals(ToastSwipeDirections.None, removed - ToastSwipeDirections.Down)

        assertEquals(
            ToastSwipeDirections.Up + ToastSwipeDirections.Down,
            ToastSwipeDirections.Vertical,
        )
        assertEquals(
            ToastSwipeDirections.Left + ToastSwipeDirections.Right,
            ToastSwipeDirections.Horizontal,
        )
        assertEquals(
            ToastSwipeDirections.Vertical + ToastSwipeDirections.Horizontal,
            ToastSwipeDirections.All,
        )
    }

    @Test
    fun orientationResolution() {
        assertNull(resolveSwipeOrientation(10f, 10f, ToastSwipeDirections.None))
        assertEquals(
            Orientation.Vertical,
            resolveSwipeOrientation(100f, 1f, ToastSwipeDirections.Down),
        )
        assertNull(resolveSwipeOrientation(100f, -1f, ToastSwipeDirections.Down))
        assertEquals(
            Orientation.Horizontal,
            resolveSwipeOrientation(-1f, 100f, ToastSwipeDirections.Left),
        )
        assertNull(resolveSwipeOrientation(1f, 100f, ToastSwipeDirections.Left))

        val downAndRight = ToastSwipeDirections.Down + ToastSwipeDirections.Right
        assertEquals(
            Orientation.Horizontal,
            resolveSwipeOrientation(20f, 5f, downAndRight),
        )
        assertEquals(
            Orientation.Vertical,
            resolveSwipeOrientation(5f, 20f, downAndRight),
        )
        // Left is disabled, Down is enabled -> prefers allowed vertical axis
        assertEquals(
            Orientation.Vertical,
            resolveSwipeOrientation(-20f, 10f, downAndRight),
        )
        // Neither left nor up is enabled -> returns null so event is not consumed
        assertNull(resolveSwipeOrientation(-20f, 0f, downAndRight))
        assertNull(resolveSwipeOrientation(-20f, -10f, downAndRight))
    }

    @Test
    fun dragOffsetClamping() {
        assertEquals(
            0f,
            clampDragOffset(0f, -20f, Orientation.Vertical, ToastSwipeDirections.Down),
        )
        assertEquals(
            20f,
            clampDragOffset(0f, 20f, Orientation.Vertical, ToastSwipeDirections.Down),
        )
        assertEquals(
            -20f,
            clampDragOffset(0f, -20f, Orientation.Vertical, ToastSwipeDirections.Up),
        )
        assertEquals(
            0f,
            clampDragOffset(0f, 20f, Orientation.Vertical, ToastSwipeDirections.Up),
        )
        assertEquals(
            -30f,
            clampDragOffset(0f, -30f, Orientation.Horizontal, ToastSwipeDirections.Left),
        )
        assertEquals(
            0f,
            clampDragOffset(0f, 30f, Orientation.Horizontal, ToastSwipeDirections.Left),
        )
        assertEquals(
            30f,
            clampDragOffset(0f, 30f, Orientation.Horizontal, ToastSwipeDirections.Right),
        )
        assertEquals(
            -15f,
            clampDragOffset(0f, -15f, Orientation.Horizontal, ToastSwipeDirections.Horizontal),
        )
    }

    @Test
    fun dismissThresholds() {
        // Zero size never dismisses
        assertEquals(
            ToastSwipeDirections.None,
            resolveDismissDirection(
                orientation = Orientation.Vertical,
                dragOffset = 100f,
                velocity = 1000f,
                width = 0,
                height = 0,
                swipeDirections = ToastSwipeDirections.All,
            ),
        )

        // Down with high velocity
        assertEquals(
            ToastSwipeDirections.Down,
            resolveDismissDirection(
                orientation = Orientation.Vertical,
                dragOffset = 25f,
                velocity = 700f,
                width = 300,
                height = 100,
                swipeDirections = ToastSwipeDirections.Down,
            ),
        )

        // Up with negative offset and negative velocity
        assertEquals(
            ToastSwipeDirections.Up,
            resolveDismissDirection(
                orientation = Orientation.Vertical,
                dragOffset = -25f,
                velocity = -700f,
                width = 300,
                height = 100,
                swipeDirections = ToastSwipeDirections.Up,
            ),
        )

        // Opposite direction disabled
        assertEquals(
            ToastSwipeDirections.None,
            resolveDismissDirection(
                orientation = Orientation.Vertical,
                dragOffset = -90f,
                velocity = -1000f,
                width = 300,
                height = 100,
                swipeDirections = ToastSwipeDirections.Down,
            ),
        )

        // Horizontal slow drag > 50% width
        assertEquals(
            ToastSwipeDirections.Left,
            resolveDismissDirection(
                orientation = Orientation.Horizontal,
                dragOffset = -160f,
                velocity = 0f,
                width = 300,
                height = 100,
                swipeDirections = ToastSwipeDirections.Left,
            ),
        )

        // Horizontal fast fling >= 15% width
        assertEquals(
            ToastSwipeDirections.Right,
            resolveDismissDirection(
                orientation = Orientation.Horizontal,
                dragOffset = 50f,
                velocity = 700f,
                width = 300,
                height = 100,
                swipeDirections = ToastSwipeDirections.Right,
            ),
        )

        // Insufficient horizontal drag
        assertEquals(
            ToastSwipeDirections.None,
            resolveDismissDirection(
                orientation = Orientation.Horizontal,
                dragOffset = 50f,
                velocity = 0f,
                width = 300,
                height = 100,
                swipeDirections = ToastSwipeDirections.Right,
            ),
        )
    }
}

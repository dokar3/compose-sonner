package com.dokar.sonner

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration

/**
 * Class representing a toast to be displayed by the Toaster.
 *
 * @param message The message content of the toast. [toString] will be called for displaying.
 * @param id Id for the toast.
 * @param icon Optional icon for the toast. It can be any type, to display it, a custom
 * icon slot must be set for the [Toaster].
 * @param action Optional action associated with the toast. If any action than [TextToastAction]
 * is set, a custom action slot must be set for the [Toaster].
 * @param type Type of the toast, specified by [ToastType].
 * @param duration Duration for which the toast should be displayed.
 */
@Immutable
class Toast(
    val message: Any,
    val id: Any = currentNanoTime(),
    val icon: Any? = null,
    val action: Any? = null,
    val type: ToastType = ToastType.Normal,
    val duration: Duration = ToasterDefaults.DurationDefault,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Toast) return false

        if (id != other.id) return false
        if (icon != other.icon) return false
        if (message != other.message) return false
        if (action != other.action) return false
        if (type != other.type) return false
        if (duration != other.duration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + message.hashCode()
        result = 31 * result + (action?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}

/**
 * The width policy for each toast.
 */
@Immutable
class ToastWidthPolicy(
    val min: Dp = Dp.Unspecified,
    val max: Dp = 380.dp,
    val fillMaxWidth: Boolean = true,
)

/**
 * The action that contains a [text] field and a [onClick] callback.
 */
@Immutable
class TextToastAction(
    val text: String,
    val onClick: (toast: Toast) -> Unit,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TextToastAction) return false

        if (text != other.text) return false
        if (onClick != other.onClick) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + onClick.hashCode()
        return result
    }
}

/**
 * Toast types.
 */
enum class ToastType {
    Normal,
    Success,
    Info,
    Warning,
    Error,
}
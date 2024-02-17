package com.dokar.sonner

/**
 * The pause strategy of the toast dismiss timer.
 */
enum class ToastDismissPause {
    /**
     * Never pause the dismiss timer.
     */
    Never,

    /**
     * Pause the dismiss timer when the toast is no longer at the front of the toast stack.
     */
    OnNotFront,

    /**
     * Pause the dismiss timer when the toast is moved out from the visible toast stack.
     */
    OnInvisible,
}
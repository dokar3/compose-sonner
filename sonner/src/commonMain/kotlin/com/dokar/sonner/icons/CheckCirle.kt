package com.dokar.sonner.icons

import androidx.compose.ui.graphics.vector.ImageVector

// androidx.compose.material.icons.filled.CheckCircle.kt

internal val CheckCircle: ImageVector
    get() {
        if (_checkCircle != null) {
            return _checkCircle!!
        }
        _checkCircle = materialIcon(name = "Filled.CheckCircle") {
            materialPath {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(10.0f, 17.0f)
                lineToRelative(-5.0f, -5.0f)
                lineToRelative(1.41f, -1.41f)
                lineTo(10.0f, 14.17f)
                lineToRelative(7.59f, -7.59f)
                lineTo(19.0f, 8.0f)
                lineToRelative(-9.0f, 9.0f)
                close()
            }
        }
        return _checkCircle!!
    }

private var _checkCircle: ImageVector? = null
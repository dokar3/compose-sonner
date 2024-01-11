package com.dokar.sonner.sample

import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _GithubIcon: ImageVector? = null

internal val GithubIcon: ImageVector
    get() {
        if (_GithubIcon != null) {
            return _GithubIcon!!
        }
        _GithubIcon = ImageVector.Builder(
            name = "GithubIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 249f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFF161614)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(127.505f, 0f)
                    curveTo(57.095f, 0f, 0f, 57.085f, 0f, 127.505f)
                    curveToRelative(0f, 56.336f, 36.534f, 104.13f, 87.196f, 120.99f)
                    curveToRelative(6.372f, 1.18f, 8.712f, -2.766f, 8.712f, -6.134f)
                    curveToRelative(0f, -3.04f, -0.119f, -13.085f, -0.173f, -23.739f)
                    curveToRelative(-35.473f, 7.713f, -42.958f, -15.044f, -42.958f, -15.044f)
                    curveToRelative(-5.8f, -14.738f, -14.157f, -18.656f, -14.157f, -18.656f)
                    curveToRelative(-11.568f, -7.914f, 0.872f, -7.752f, 0.872f, -7.752f)
                    curveToRelative(12.804f, 0.9f, 19.546f, 13.14f, 19.546f, 13.14f)
                    curveToRelative(11.372f, 19.493f, 29.828f, 13.857f, 37.104f, 10.6f)
                    curveToRelative(1.144f, -8.242f, 4.449f, -13.866f, 8.095f, -17.05f)
                    curveToRelative(-28.32f, -3.225f, -58.092f, -14.158f, -58.092f, -63.014f)
                    curveToRelative(0f, -13.92f, 4.981f, -25.295f, 13.138f, -34.224f)
                    curveToRelative(-1.324f, -3.212f, -5.688f, -16.18f, 1.235f, -33.743f)
                    curveToRelative(0f, 0f, 10.707f, -3.427f, 35.073f, 13.07f)
                    curveToRelative(10.17f, -2.826f, 21.078f, -4.242f, 31.914f, -4.29f)
                    curveToRelative(10.836f, 0.048f, 21.752f, 1.464f, 31.942f, 4.29f)
                    curveToRelative(24.337f, -16.497f, 35.029f, -13.07f, 35.029f, -13.07f)
                    curveToRelative(6.94f, 17.563f, 2.574f, 30.531f, 1.25f, 33.743f)
                    curveToRelative(8.175f, 8.929f, 13.122f, 20.303f, 13.122f, 34.224f)
                    curveToRelative(0f, 48.972f, -29.828f, 59.756f, -58.22f, 62.912f)
                    curveToRelative(4.573f, 3.957f, 8.648f, 11.717f, 8.648f, 23.612f)
                    curveToRelative(0f, 17.06f, -0.148f, 30.791f, -0.148f, 34.991f)
                    curveToRelative(0f, 3.393f, 2.295f, 7.369f, 8.759f, 6.117f)
                    curveToRelative(50.634f, -16.879f, 87.122f, -64.656f, 87.122f, -120.973f)
                    curveTo(255.009f, 57.085f, 197.922f, 0f, 127.505f, 0f)
                }
                path(
                    fill = SolidColor(Color(0xFF161614)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(47.755f, 181.634f)
                    curveToRelative(-0.28f, 0.633f, -1.278f, 0.823f, -2.185f, 0.389f)
                    curveToRelative(-0.925f, -0.416f, -1.445f, -1.28f, -1.145f, -1.916f)
                    curveToRelative(0.275f, -0.652f, 1.273f, -0.834f, 2.196f, -0.396f)
                    curveToRelative(0.927f, 0.415f, 1.455f, 1.287f, 1.134f, 1.923f)
                    moveTo(54.027f, 187.23f)
                    curveToRelative(-0.608f, 0.564f, -1.797f, 0.302f, -2.604f, -0.589f)
                    curveToRelative(-0.834f, -0.889f, -0.99f, -2.077f, -0.373f, -2.65f)
                    curveToRelative(0.627f, -0.563f, 1.78f, -0.3f, 2.616f, 0.59f)
                    curveToRelative(0.834f, 0.899f, 0.996f, 2.08f, 0.36f, 2.65f)
                    moveTo(58.33f, 194.39f)
                    curveToRelative(-0.782f, 0.543f, -2.06f, 0.034f, -2.849f, -1.1f)
                    curveToRelative(-0.781f, -1.133f, -0.781f, -2.493f, 0.017f, -3.038f)
                    curveToRelative(0.792f, -0.545f, 2.05f, -0.055f, 2.85f, 1.07f)
                    curveToRelative(0.78f, 1.153f, 0.78f, 2.513f, -0.019f, 3.069f)
                    moveTo(65.606f, 202.683f)
                    curveToRelative(-0.699f, 0.77f, -2.187f, 0.564f, -3.277f, -0.488f)
                    curveToRelative(-1.114f, -1.028f, -1.425f, -2.487f, -0.724f, -3.258f)
                    curveToRelative(0.707f, -0.772f, 2.204f, -0.555f, 3.302f, 0.488f)
                    curveToRelative(1.107f, 1.026f, 1.445f, 2.496f, 0.7f, 3.258f)
                    moveTo(75.01f, 205.483f)
                    curveToRelative(-0.307f, 0.998f, -1.741f, 1.452f, -3.185f, 1.028f)
                    curveToRelative(-1.442f, -0.437f, -2.386f, -1.607f, -2.095f, -2.616f)
                    curveToRelative(0.3f, -1.005f, 1.74f, -1.478f, 3.195f, -1.024f)
                    curveToRelative(1.44f, 0.435f, 2.386f, 1.596f, 2.086f, 2.612f)
                    moveTo(85.714f, 206.67f)
                    curveToRelative(0.036f, 1.052f, -1.189f, 1.924f, -2.705f, 1.943f)
                    curveToRelative(-1.525f, 0.033f, -2.758f, -0.818f, -2.774f, -1.852f)
                    curveToRelative(0f, -1.062f, 1.197f, -1.926f, 2.721f, -1.951f)
                    curveToRelative(1.516f, -0.03f, 2.758f, 0.815f, 2.758f, 1.86f)
                    moveTo(96.228f, 206.267f)
                    curveToRelative(0.182f, 1.026f, -0.872f, 2.08f, -2.377f, 2.36f)
                    curveToRelative(-1.48f, 0.27f, -2.85f, -0.363f, -3.039f, -1.38f)
                    curveToRelative(-0.184f, -1.052f, 0.89f, -2.105f, 2.367f, -2.378f)
                    curveToRelative(1.508f, -0.262f, 2.857f, 0.355f, 3.049f, 1.398f)
                }
            }
        }.build()
        return _GithubIcon!!
    }


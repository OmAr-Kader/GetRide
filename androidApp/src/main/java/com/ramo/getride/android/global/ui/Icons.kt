package com.ramo.getride.android.global.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberExitToApp(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "exit_to_app",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16.375f, 26.958f)
                quadToRelative(-0.375f, -0.416f, -0.375f, -1f)
                quadToRelative(0f, -0.583f, 0.375f, -0.958f)
                lineToRelative(3.667f, -3.708f)
                horizontalLineToRelative(-13.5f)
                quadToRelative(-0.584f, 0f, -0.959f, -0.375f)
                reflectiveQuadTo(5.208f, 20f)
                quadToRelative(0f, -0.542f, 0.375f, -0.938f)
                quadToRelative(0.375f, -0.395f, 0.959f, -0.395f)
                horizontalLineToRelative(13.5f)
                lineToRelative(-3.709f, -3.709f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.937f)
                quadToRelative(0f, -0.563f, 0.417f, -0.979f)
                quadToRelative(0.375f, -0.417f, 0.958f, -0.417f)
                quadToRelative(0.584f, 0f, 0.959f, 0.375f)
                lineToRelative(6.041f, 6.083f)
                quadToRelative(0.209f, 0.209f, 0.313f, 0.438f)
                quadToRelative(0.104f, 0.229f, 0.104f, 0.479f)
                quadToRelative(0f, 0.292f, -0.104f, 0.5f)
                quadToRelative(-0.104f, 0.208f, -0.313f, 0.417f)
                lineTo(18.292f, 27f)
                quadToRelative(-0.417f, 0.417f, -0.959f, 0.396f)
                quadToRelative(-0.541f, -0.021f, -0.958f, -0.438f)
                close()
                moveTo(7.833f, 34.75f)
                quadToRelative(-1.041f, 0f, -1.833f, -0.792f)
                quadToRelative(-0.792f, -0.791f, -0.792f, -1.875f)
                verticalLineToRelative(-6.791f)
                quadToRelative(0f, -0.584f, 0.375f, -0.959f)
                reflectiveQuadToRelative(0.959f, -0.375f)
                quadToRelative(0.541f, 0f, 0.916f, 0.375f)
                reflectiveQuadToRelative(0.375f, 0.959f)
                verticalLineToRelative(6.791f)
                horizontalLineToRelative(24.292f)
                verticalLineTo(7.833f)
                horizontalLineTo(7.833f)
                verticalLineToRelative(6.875f)
                quadToRelative(0f, 0.584f, -0.375f, 0.959f)
                reflectiveQuadToRelative(-0.916f, 0.375f)
                quadToRelative(-0.584f, 0f, -0.959f, -0.375f)
                reflectiveQuadToRelative(-0.375f, -0.959f)
                verticalLineTo(7.833f)
                quadToRelative(0f, -1.083f, 0.792f, -1.854f)
                quadToRelative(0.792f, -0.771f, 1.833f, -0.771f)
                horizontalLineToRelative(24.292f)
                quadToRelative(1.083f, 0f, 1.875f, 0.771f)
                reflectiveQuadToRelative(0.792f, 1.854f)
                verticalLineToRelative(24.25f)
                quadToRelative(0f, 1.084f, -0.792f, 1.875f)
                quadToRelative(-0.792f, 0.792f, -1.875f, 0.792f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberSearch(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "search",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(31.917f, 33.792f)
                lineToRelative(-9.75f, -9.75f)
                quadToRelative(-1.209f, 1.041f, -2.855f, 1.625f)
                quadToRelative(-1.645f, 0.583f, -3.479f, 0.583f)
                quadToRelative(-4.458f, 0f, -7.521f, -3.083f)
                quadToRelative(-3.062f, -3.084f, -3.062f, -7.459f)
                reflectiveQuadToRelative(3.062f, -7.437f)
                quadToRelative(3.063f, -3.063f, 7.48f, -3.063f)
                quadToRelative(4.375f, 0f, 7.437f, 3.063f)
                quadToRelative(3.063f, 3.062f, 3.063f, 7.479f)
                quadToRelative(0f, 1.75f, -0.584f, 3.396f)
                quadToRelative(-0.583f, 1.646f, -1.666f, 3.021f)
                lineToRelative(9.833f, 9.75f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.916f)
                quadToRelative(0f, 0.542f, -0.417f, 0.959f)
                quadToRelative(-0.416f, 0.375f, -0.979f, 0.375f)
                quadToRelative(-0.562f, 0f, -0.937f, -0.375f)
                close()
                moveTo(15.792f, 23.625f)
                quadToRelative(3.291f, 0f, 5.583f, -2.313f)
                quadToRelative(2.292f, -2.312f, 2.292f, -5.604f)
                quadToRelative(0f, -3.291f, -2.292f, -5.583f)
                quadToRelative(-2.292f, -2.292f, -5.583f, -2.292f)
                quadToRelative(-3.292f, 0f, -5.604f, 2.313f)
                quadToRelative(-2.313f, 2.312f, -2.313f, 5.562f)
                quadToRelative(0f, 3.292f, 2.313f, 5.604f)
                quadToRelative(2.312f, 2.313f, 5.604f, 2.313f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberComment(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "comment",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(11.5f, 23.208f)
                horizontalLineToRelative(17.042f)
                quadToRelative(0.5f, 0f, 0.896f, -0.396f)
                quadToRelative(0.395f, -0.395f, 0.395f, -0.937f)
                reflectiveQuadToRelative(-0.395f, -0.937f)
                quadToRelative(-0.396f, -0.396f, -0.896f, -0.396f)
                horizontalLineTo(11.5f)
                quadToRelative(-0.583f, 0f, -0.958f, 0.396f)
                quadToRelative(-0.375f, 0.395f, -0.375f, 0.937f)
                reflectiveQuadToRelative(0.375f, 0.937f)
                quadToRelative(0.375f, 0.396f, 0.958f, 0.396f)
                close()
                moveToRelative(0f, -5.25f)
                horizontalLineToRelative(17.042f)
                quadToRelative(0.5f, 0f, 0.896f, -0.375f)
                quadToRelative(0.395f, -0.375f, 0.395f, -0.916f)
                quadToRelative(0f, -0.584f, -0.395f, -0.959f)
                quadToRelative(-0.396f, -0.375f, -0.896f, -0.375f)
                horizontalLineTo(11.5f)
                quadToRelative(-0.583f, 0f, -0.958f, 0.396f)
                reflectiveQuadToRelative(-0.375f, 0.938f)
                quadToRelative(0f, 0.541f, 0.375f, 0.916f)
                reflectiveQuadToRelative(0.958f, 0.375f)
                close()
                moveToRelative(0f, -5.208f)
                horizontalLineToRelative(17.042f)
                quadToRelative(0.5f, 0f, 0.896f, -0.396f)
                quadToRelative(0.395f, -0.396f, 0.395f, -0.937f)
                quadToRelative(0f, -0.542f, -0.395f, -0.917f)
                quadToRelative(-0.396f, -0.375f, -0.896f, -0.375f)
                horizontalLineTo(11.5f)
                quadToRelative(-0.583f, 0f, -0.958f, 0.375f)
                reflectiveQuadToRelative(-0.375f, 0.917f)
                quadToRelative(0f, 0.583f, 0.375f, 0.958f)
                reflectiveQuadToRelative(0.958f, 0.375f)
                close()
                moveToRelative(22.667f, 21.292f)
                lineToRelative(-4.292f, -4.292f)
                horizontalLineTo(6.25f)
                quadToRelative(-1.042f, 0f, -1.833f, -0.792f)
                quadToRelative(-0.792f, -0.791f, -0.792f, -1.833f)
                verticalLineTo(6.208f)
                quadToRelative(0f, -1.041f, 0.792f, -1.833f)
                quadToRelative(0.791f, -0.792f, 1.833f, -0.792f)
                horizontalLineToRelative(27.5f)
                quadToRelative(1.083f, 0f, 1.854f, 0.792f)
                quadToRelative(0.771f, 0.792f, 0.771f, 1.833f)
                verticalLineToRelative(26.917f)
                quadToRelative(0f, 0.833f, -0.792f, 1.187f)
                quadToRelative(-0.791f, 0.355f, -1.416f, -0.27f)
                close()
                moveTo(6.25f, 6.208f)
                verticalLineToRelative(20.917f)
                horizontalLineTo(31f)
                lineToRelative(2.75f, 2.75f)
                verticalLineTo(6.208f)
                horizontalLineTo(6.25f)
                close()
                moveToRelative(0f, 0f)
                verticalLineToRelative(23.667f)
                verticalLineTo(6.208f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberChat(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "Chat",
            defaultWidth = 256.dp,
            defaultHeight = 256.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(14.1f, 11.2f)
                curveToRelative(-1.5f, 0.8f, -2.4f, 1.6f, -3.1f, 3.1f)
                lineToRelative(-1.1f, 2f)
                verticalLineTo(84f)
                curveToRelative(0f, 64.9f, 0f, 67.6f, 0.9f, 69.4f)
                curveToRelative(0.5f, 1.1f, 1.6f, 2.4f, 2.4f, 3f)
                curveToRelative(1.6f, 1.1f, 1.7f, 1.1f, 13.9f, 1.2f)
                lineToRelative(12.3f, 0.1f)
                verticalLineToRelative(18.3f)
                verticalLineToRelative(18.3f)
                lineTo(57.8f, 176f)
                lineToRelative(18.3f, -18.3f)
                horizontalLineTo(139f)
                horizontalLineToRelative(62.8f)
                lineToRelative(-0.3f, -70.4f)
                curveToRelative(-0.1f, -38.7f, -0.4f, -71f, -0.5f, -71.8f)
                curveToRelative(-0.2f, -1.6f, -2.5f, -4f, -4.5f, -4.7f)
                curveToRelative(-0.9f, -0.4f, -28.8f, -0.5f, -90.9f, -0.5f)
                horizontalLineTo(16.2f)
                lineTo(14.1f, 11.2f)
                close()
                moveTo(164.8f, 61.7f)
                lineToRelative(0.1f, 7.5f)
                horizontalLineToRelative(-59f)
                horizontalLineToRelative(-59f)
                verticalLineToRelative(-7.6f)
                verticalLineTo(54f)
                lineToRelative(58.9f, 0.1f)
                lineToRelative(58.9f, 0.1f)
                lineTo(164.8f, 61.7f)
                close()
                moveTo(164.4f, 106.1f)
                verticalLineToRelative(7.4f)
                horizontalLineToRelative(-58.8f)
                horizontalLineTo(46.9f)
                verticalLineToRelative(-7.4f)
                verticalLineToRelative(-7.4f)
                horizontalLineToRelative(58.8f)
                horizontalLineToRelative(58.8f)
                lineTo(164.4f, 106.1f)
                lineTo(164.4f, 106.1f)
                close()
            }
            path(
                fill = SolidColor(color),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(216.5f, 135.6f)
                verticalLineToRelative(36.9f)
                horizontalLineToRelative(-62.8f)
                horizontalLineTo(90.8f)
                lineToRelative(0.2f, 19.2f)
                curveToRelative(0.2f, 21.2f, 0.2f, 21.3f, 3f, 23.3f)
                curveToRelative(1.4f, 1f, 1.7f, 1f, 23.1f, 1.3f)
                curveToRelative(11.9f, 0.2f, 33.8f, 0.3f, 48.6f, 0.4f)
                lineToRelative(27f, 0f)
                lineToRelative(14.5f, 14.5f)
                curveToRelative(8f, 8f, 14.7f, 14.5f, 14.8f, 14.5f)
                curveToRelative(0.2f, 0f, 0.4f, -6.5f, 0.4f, -14.5f)
                verticalLineToRelative(-14.5f)
                horizontalLineToRelative(8.7f)
                curveToRelative(8.1f, 0f, 8.8f, -0.1f, 10.7f, -1.1f)
                curveToRelative(1.5f, -0.7f, 2.4f, -1.6f, 3.1f, -3.1f)
                lineToRelative(1f, -2f)
                verticalLineToRelative(-52.8f)
                verticalLineToRelative(-52.8f)
                lineToRelative(-1f, -2f)
                curveToRelative(-2f, -3.9f, -2f, -3.9f, -16.1f, -4.1f)
                lineToRelative(-12.4f, -0.1f)
                lineTo(216.5f, 135.6f)
                lineTo(216.5f, 135.6f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberSocial(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "Safety collection",
            defaultWidth = 40.dp,
            defaultHeight = 40.dp,
            viewportWidth = 40f,
            viewportHeight = 40f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFFE8D47B)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(35.5f, 15.5f)
                    arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 31.5f, 19.5f)
                    arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 27.5f, 15.5f)
                    arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 35.5f, 15.5f)
                    close()
                }
                group {
                    path(
                        fill = SolidColor(Color(0xFFBA9B48)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(31.5f, 12f)
                        curveToRelative(1.93f, 0f, 3.5f, 1.57f, 3.5f, 3.5f)
                        reflectiveCurveTo(33.43f, 19f, 31.5f, 19f)
                        reflectiveCurveTo(28f, 17.43f, 28f, 15.5f)
                        reflectiveCurveTo(29.57f, 12f, 31.5f, 12f)
                        moveTo(31.5f, 11f)
                        curveToRelative(-2.485f, 0f, -4.5f, 2.015f, -4.5f, 4.5f)
                        curveToRelative(0f, 2.485f, 2.015f, 4.5f, 4.5f, 4.5f)
                        reflectiveCurveToRelative(4.5f, -2.015f, 4.5f, -4.5f)
                        curveTo(36f, 13.015f, 33.985f, 11f, 31.5f, 11f)
                        lineTo(31.5f, 11f)
                        close()
                    }
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF99C99E)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(24.516f, 28.5f)
                    curveToRelative(0.241f, -3.903f, 3.281f, -7f, 6.984f, -7f)
                    reflectiveCurveToRelative(6.743f, 3.097f, 6.984f, 7f)
                    horizontalLineTo(24.516f)
                    close()
                }
                group {
                    path(
                        fill = SolidColor(Color(0xFF5E9C76)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(31.5f, 22f)
                        curveToRelative(3.269f, 0f, 5.982f, 2.612f, 6.434f, 6f)
                        horizontalLineTo(25.066f)
                        curveTo(25.518f, 24.612f, 28.231f, 22f, 31.5f, 22f)
                        moveTo(31.5f, 21f)
                        curveToRelative(-4.142f, 0f, -7.5f, 3.582f, -7.5f, 8f)
                        horizontalLineToRelative(15f)
                        curveTo(39f, 24.582f, 35.642f, 21f, 31.5f, 21f)
                        lineTo(31.5f, 21f)
                        close()
                    }
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFFE8D47B)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.5f, 15.5f)
                    arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.5f, 19.5f)
                    arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.5f, 15.5f)
                    arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.5f, 15.5f)
                    close()
                }
                group {
                    path(
                        fill = SolidColor(Color(0xFFBA9B48)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(8.5f, 12f)
                        curveToRelative(1.93f, 0f, 3.5f, 1.57f, 3.5f, 3.5f)
                        reflectiveCurveTo(10.43f, 19f, 8.5f, 19f)
                        reflectiveCurveTo(5f, 17.43f, 5f, 15.5f)
                        reflectiveCurveTo(6.57f, 12f, 8.5f, 12f)
                        moveTo(8.5f, 11f)
                        curveTo(6.015f, 11f, 4f, 13.015f, 4f, 15.5f)
                        curveTo(4f, 17.985f, 6.015f, 20f, 8.5f, 20f)
                        reflectiveCurveToRelative(4.5f, -2.015f, 4.5f, -4.5f)
                        curveTo(13f, 13.015f, 10.985f, 11f, 8.5f, 11f)
                        lineTo(8.5f, 11f)
                        close()
                    }
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF99C99E)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(1.516f, 28.5f)
                    curveToRelative(0.241f, -3.903f, 3.281f, -7f, 6.984f, -7f)
                    reflectiveCurveToRelative(6.743f, 3.097f, 6.984f, 7f)
                    horizontalLineTo(1.516f)
                    close()
                }
                group {
                    path(
                        fill = SolidColor(Color(0xFF5E9C76)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(8.5f, 22f)
                        curveToRelative(3.269f, 0f, 5.982f, 2.612f, 6.434f, 6f)
                        horizontalLineTo(2.066f)
                        curveTo(2.518f, 24.612f, 5.231f, 22f, 8.5f, 22f)
                        moveTo(8.5f, 21f)
                        curveTo(4.358f, 21f, 1f, 24.582f, 1f, 29f)
                        horizontalLineToRelative(15f)
                        curveTo(16f, 24.582f, 12.642f, 21f, 8.5f, 21f)
                        lineTo(8.5f, 21f)
                        close()
                    }
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFFE8D47B)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(24.5f, 19f)
                    arcTo(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 23.5f)
                    arcTo(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15.5f, 19f)
                    arcTo(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 24.5f, 19f)
                    close()
                }
                group {
                    path(
                        fill = SolidColor(Color(0xFFBA9B48)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(20f, 15f)
                        curveToRelative(2.206f, 0f, 4f, 1.794f, 4f, 4f)
                        reflectiveCurveToRelative(-1.794f, 4f, -4f, 4f)
                        reflectiveCurveToRelative(-4f, -1.794f, -4f, -4f)
                        reflectiveCurveTo(17.794f, 15f, 20f, 15f)
                        moveTo(20f, 14f)
                        curveToRelative(-2.761f, 0f, -5f, 2.239f, -5f, 5f)
                        reflectiveCurveToRelative(2.239f, 5f, 5f, 5f)
                        reflectiveCurveToRelative(5f, -2.239f, 5f, -5f)
                        reflectiveCurveTo(22.761f, 14f, 20f, 14f)
                        lineTo(20f, 14f)
                        close()
                    }
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFFD8F0DA)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(12.5f, 33.5f)
                    verticalLineToRelative(-0.786f)
                    curveToRelative(0f, -3.978f, 3.364f, -7.214f, 7.5f, -7.214f)
                    reflectiveCurveToRelative(7.5f, 3.236f, 7.5f, 7.214f)
                    verticalLineTo(33.5f)
                    horizontalLineTo(12.5f)
                    close()
                }
                group {
                    path(
                        fill = SolidColor(Color(0xFF5E9C76)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero
                    ) {
                        moveTo(20f, 26f)
                        curveToRelative(3.86f, 0f, 7f, 3.012f, 7f, 6.714f)
                        verticalLineTo(33f)
                        horizontalLineTo(13f)
                        verticalLineToRelative(-0.286f)
                        curveTo(13f, 29.012f, 16.14f, 26f, 20f, 26f)
                        moveTo(20f, 25f)
                        curveToRelative(-4.418f, 0f, -8f, 3.454f, -8f, 7.714f)
                        curveToRelative(0f, 0f, 0f, 0.383f, 0f, 1.286f)
                        horizontalLineToRelative(16f)
                        curveToRelative(0f, -0.903f, 0f, -1.286f, 0f, -1.286f)
                        curveTo(28f, 28.454f, 24.418f, 25f, 20f, 25f)
                        lineTo(20f, 25f)
                        close()
                    }
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(2.715f, 1.032f)
                    horizontalLineTo(5.248f)
                    verticalLineTo(6.931f)
                    horizontalLineTo(2.715f)
                    verticalLineTo(1.032f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(9f, 2f)
                    lineTo(9f, 9f)
                    lineTo(2f, 9f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(1.032f, 34.752f)
                    horizontalLineTo(6.931f)
                    verticalLineTo(37.285f)
                    horizontalLineTo(1.032f)
                    verticalLineTo(34.752f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(2f, 31f)
                    lineTo(9f, 31f)
                    lineTo(9f, 38f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(33.069f, 2.715f)
                    horizontalLineTo(38.968f)
                    verticalLineTo(5.248f)
                    horizontalLineTo(33.069f)
                    verticalLineTo(2.715f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(31f, 2f)
                    lineTo(31f, 9f)
                    lineTo(38f, 9f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(34.752f, 33.069f)
                    horizontalLineTo(37.285f)
                    verticalLineTo(38.968f)
                    horizontalLineTo(34.752f)
                    verticalLineTo(33.069f)
                    close()
                }
            }
            group {
                path(
                    fill = SolidColor(Color(0xFF4E7AB5)),
                    fillAlpha = 1.0f,
                    stroke = null,
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 1.0f,
                    strokeLineCap = StrokeCap.Butt,
                    strokeLineJoin = StrokeJoin.Miter,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero
                ) {
                    moveTo(38f, 31f)
                    lineTo(31f, 31f)
                    lineTo(31f, 38f)
                    close()
                }
            }
        }.build()
    }
}

@Composable
fun rememberProfile(color: Color): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "Profile",
            defaultWidth = 256.dp,
            defaultHeight = 256.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                fill = SolidColor(color),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(246f, 231.2f)
                lineTo(246f, 231.2f)
                curveToRelative(0f, 6.1f, -5f, 11.1f, -11.1f, 11.1f)
                horizontalLineTo(21.1f)
                curveToRelative(-6.1f, 0f, -11.1f, -5f, -11.1f, -11.1f)
                verticalLineToRelative(0f)
                lineToRelative(0f, 0f)
                curveToRelative(0f, 0f, 0f, -40.3f, 29.5f, -55f)
                curveToRelative(18.6f, -9.3f, 11.5f, -1.7f, 34.3f, -11.2f)
                curveToRelative(22.9f, -9.5f, 28.3f, -12.8f, 28.3f, -12.8f)
                lineToRelative(0.2f, -21.8f)
                curveToRelative(0f, 0f, -8.6f, -6.6f, -11.2f, -27.1f)
                curveTo(85.7f, 104.8f, 84f, 97f, 83.6f, 92f)
                curveToRelative(-0.3f, -4.8f, -3.1f, -19.9f, 3.4f, -18.5f)
                curveToRelative(-1.3f, -10.1f, -2.3f, -19.1f, -1.8f, -23.9f)
                curveToRelative(1.6f, -16.9f, 17.9f, -34.5f, 42.9f, -35.8f)
                curveToRelative(29.4f, 1.3f, 41.1f, 18.9f, 42.7f, 35.8f)
                curveToRelative(0.5f, 4.8f, -0.6f, 13.9f, -1.9f, 23.9f)
                curveToRelative(6.6f, -1.3f, 3.7f, 13.7f, 3.4f, 18.5f)
                curveToRelative(-0.3f, 5f, -2.1f, 12.8f, -7.4f, 11.3f)
                curveToRelative(-2.7f, 20.5f, -11.2f, 27f, -11.2f, 27f)
                lineToRelative(0.2f, 21.7f)
                curveToRelative(0f, 0f, 5.4f, 3.1f, 28.3f, 12.6f)
                curveToRelative(22.9f, 9.5f, 15.7f, 2.4f, 34.3f, 11.7f)
                curveTo(246f, 190.9f, 246f, 231.2f, 246f, 231.2f)
                lineTo(246f, 231.2f)
                lineTo(246f, 231.2f)
                close()
            }
        }.build()
    }
}

@Composable
fun rememberDoneAll(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "done_all",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.083f, 29.25f)
                quadToRelative(-0.25f, 0f, -0.458f, -0.083f)
                quadToRelative(-0.208f, -0.084f, -0.417f, -0.292f)
                lineToRelative(-7.333f, -7.333f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.959f)
                quadToRelative(0f, -0.583f, 0.375f, -0.958f)
                quadToRelative(0.417f, -0.417f, 0.937f, -0.396f)
                quadToRelative(0.521f, 0.021f, 0.938f, 0.396f)
                lineToRelative(6.375f, 6.417f)
                lineTo(14f, 27.917f)
                lineToRelative(-0.958f, 0.916f)
                quadToRelative(-0.209f, 0.25f, -0.438f, 0.334f)
                quadToRelative(-0.229f, 0.083f, -0.521f, 0.083f)
                close()
                moveToRelative(7.5f, 0f)
                quadToRelative(-0.25f, 0f, -0.479f, -0.083f)
                quadToRelative(-0.229f, -0.084f, -0.437f, -0.292f)
                lineToRelative(-7.334f, -7.333f)
                quadToRelative(-0.375f, -0.375f, -0.375f, -0.959f)
                quadToRelative(0f, -0.583f, 0.375f, -0.958f)
                quadToRelative(0.417f, -0.375f, 0.938f, -0.375f)
                quadToRelative(0.521f, 0f, 0.896f, 0.375f)
                lineToRelative(6.416f, 6.417f)
                lineToRelative(14.709f, -14.75f)
                quadToRelative(0.375f, -0.375f, 0.937f, -0.375f)
                quadToRelative(0.563f, 0f, 0.938f, 0.375f)
                quadToRelative(0.416f, 0.416f, 0.416f, 0.958f)
                reflectiveQuadToRelative(-0.416f, 0.917f)
                lineTo(20.5f, 28.875f)
                quadToRelative(-0.208f, 0.208f, -0.438f, 0.292f)
                quadToRelative(-0.229f, 0.083f, -0.479f, 0.083f)
                close()
                moveToRelative(0f, -6.917f)
                lineToRelative(-1.875f, -1.875f)
                lineToRelative(9.125f, -9.166f)
                quadToRelative(0.375f, -0.375f, 0.938f, -0.375f)
                quadToRelative(0.562f, 0f, 0.937f, 0.416f)
                quadToRelative(0.375f, 0.375f, 0.375f, 0.938f)
                quadToRelative(0f, 0.562f, -0.375f, 0.937f)
                close()
            }
        }.build()
    }
}

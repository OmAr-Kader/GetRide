package com.ramo.getride.android.global.base

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Theme(
    val isDarkMode: Boolean,
    val isDarkStatusBarText: Boolean,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val backDark: Color,
    val backDarkSec: Color,
    val backgroundPrimary: Color,
    val backGreyTrans: Color,
    val textColor: Color,
    val textForPrimaryColor: Color,
    val textGrayColor: Color,
    val error: Color,
    val textHintColor: Color,
    val pri: Color
) {
    @Suppress("unused")
    val priAlpha: Color = pri.copy(alpha = 0.14F)
    val textHintAlpha = textHintColor.copy(alpha = 0.5F)
    val backDarkAlpha = backDark.copy(alpha = 0.5F)
    val seenColor = if (isDarkMode) Color(0xFF4FC3F7) else Color.Blue
}

fun generateTheme(isDarkMode: Boolean): Theme {
    return if (isDarkMode) {
        Theme(
            isDarkMode = true,
            isDarkStatusBarText = false,
            primary = Color(red = 255, green = 74, blue = 74),//rgb(255, 74, 74)
            secondary = Color(red = 231, green = 83, blue = 83, alpha = 255),
            background = Color(red = 31, green = 31, blue = 31, alpha = 255),
            backDark = Color(50, 50, 50),
            backDarkSec = Color(100, 100, 100),
            backgroundPrimary = Color(red = 31, green = 31, blue = 31, alpha = 255).darker(),
            backGreyTrans = Color(85, 85, 85, 85),
            textColor = Color.White,
            textForPrimaryColor = Color.Black,
            textGrayColor = Color(143, 143, 143),
            error = Color(red = 255, green = 21, blue = 21),
            textHintColor = Color(red = 204, green = 204, blue = 204),
            pri = Color(102, 158, 255)
        )
    } else {
        Theme(
            isDarkMode = false,
            isDarkStatusBarText = true,
            primary = Color(red = 255, green = 74, blue = 74),//rgb(255, 74, 74)
            secondary = Color(red = 231, green = 83, blue = 83, alpha = 255),
            background = Color.White,
            backDark = Color(215, 215, 215),
            backDarkSec = Color(200, 200, 200),
            backgroundPrimary = Color.White.darker(),
            backGreyTrans = Color(170, 170, 170, 85),
            textColor = Color.Black,
            textForPrimaryColor = Color.Black,
            textGrayColor = Color(112, 112, 112),
            error = Color(red = 155, green = 0, blue = 0),
            textHintColor = Color(red = 68, green = 68, blue = 68),
            pri = Color(102, 158, 255)
        )
    }
}


@Composable
fun MyApplicationTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = theme.primary,
            secondary = theme.secondary,
            background = theme.background,
            onBackground = theme.textHintColor,
        )
    } else {
        lightColorScheme(
            primary = theme.primary,
            secondary = theme.secondary,
            background = theme.background,
            onBackground = theme.textHintColor,
        )
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

package com.klim.typeless.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.klim.typeless.ui.theme.LocalDarkTheme

fun Modifier.neumorph(
    bgColor: Color,
    lightShadow: Color,
    darkShadow: Color,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 8.dp
): Modifier = this
    .drawBehind {
        val radiusPx = cornerRadius.toPx()
        val elevPx = elevation.toPx()
        drawIntoCanvas { canvas ->
            val lightPaint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(elevPx, -elevPx * 0.6f, -elevPx * 0.6f, lightShadow.toArgb())
                }
            }
            val darkPaint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(elevPx, elevPx * 0.6f, elevPx * 0.6f, darkShadow.toArgb())
                }
            }
            canvas.drawRoundRect(0f, 0f, size.width, size.height, radiusPx, radiusPx, lightPaint)
            canvas.drawRoundRect(0f, 0f, size.width, size.height, radiusPx, radiusPx, darkPaint)
        }
    }
    .clip(RoundedCornerShape(cornerRadius))
    .background(bgColor)

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    val darkTheme = LocalDarkTheme.current
    val bgColor = if (darkTheme) Color(0xFF1E1E2A) else Color(0xFFE8E8EC)
    val lightShadow = if (darkTheme) Color(0xFF2D2D45) else Color(0xFFFFFFFF)
    val darkShadow = if (darkTheme) Color(0xFF0F0F18) else Color(0xFFC8C8CE)

    Box(
        modifier = modifier
            .neumorph(bgColor, lightShadow, darkShadow)
            .clickable { onClick() },
        content = content
    )
}
package com.klim.typeless.ui.home

import androidx.compose.ui.graphics.Color

val folderColors = listOf(
    Color(0xFF7C3AED),
    Color(0xFF2563EB),
    Color(0xFF059669),
    Color(0xFFDC2626),
    Color(0xFFD97706),
    Color(0xFFDB2777),
    Color(0xFF0891B2),
    Color(0xFF65A30D),
)

fun folderColorByIndex(index: Int): Color = folderColors[index % folderColors.size]

fun folderColorFromHex(hex: Long): Color = Color(hex)

fun Color.toHex(): Long = (value shr 32).toLong()
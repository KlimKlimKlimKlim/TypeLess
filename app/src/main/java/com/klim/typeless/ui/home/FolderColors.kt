package com.klim.typeless.ui.home

import androidx.compose.ui.graphics.Color

val folderColors = listOf(
    Color(0xFFFFE0B2),
    Color(0xFFC8E6C9),
    Color(0xFFB3E5FC),
    Color(0xFFF8BBD0),
    Color(0xFFD1C4E9),
    Color(0xFFFFF9C4),
    Color(0xFFB2DFDB),
    Color(0xFFFFCCBC),
)

val folderColorsDark = listOf(
    Color(0xFF4A3B1F),
    Color(0xFF29402C),
    Color(0xFF1F3A47),
    Color(0xFF442733),
    Color(0xFF332C47),
    Color(0xFF454020),
    Color(0xFF1E3B38),
    Color(0xFF45291F),
)

fun folderColorByIndex(index: Int, isDark: Boolean = false): Color =
    if (isDark) folderColorsDark[index % folderColorsDark.size]
    else folderColors[index % folderColors.size]

fun folderColorFromHex(hex: Long): Color = Color(hex)

fun Color.toHex(): Long = (value shr 32).toLong()
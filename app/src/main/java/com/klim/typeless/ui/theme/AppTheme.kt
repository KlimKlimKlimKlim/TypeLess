package com.klim.typeless.ui.theme

enum class AppTheme {
    LIGHT, DARK, SYSTEM;

    fun label(): String = when (this) {
        LIGHT  -> "Светлая"
        DARK   -> "Тёмная"
        SYSTEM -> "Системная"
    }
}
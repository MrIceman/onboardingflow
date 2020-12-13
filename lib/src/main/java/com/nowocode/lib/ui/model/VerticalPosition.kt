package com.nowocode.lib.ui.model

enum class VerticalPosition {
    TOP,
    BOTTOM
}

fun VerticalPosition.inverse(): VerticalPosition = when (this) {
    VerticalPosition.TOP -> VerticalPosition.BOTTOM
    else -> VerticalPosition.TOP
}
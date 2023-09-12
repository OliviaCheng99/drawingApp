package com.cs6018.phase1

import android.graphics.Color
import android.graphics.Path

data class DrawPath(
    val path: Path = Path(),
    val color: Int = Color.BLACK,
    val strokeWidth: Float = 5f
)

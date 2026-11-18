package com.example.ui.render

import androidx.compose.ui.geometry.Offset

/**
 * Mathematical utilities for 2:1 isometric projection.
 */
object IsoMath {
    // Standard 2:1 isometric ratio
    const val TILE_WIDTH = 72f
    const val TILE_HEIGHT = 36f
    const val HALF_TILE_W = TILE_WIDTH / 2f
    const val HALF_TILE_H = TILE_HEIGHT / 2f
    const val HEIGHT_SCALE = 32f

    /**
     * Converts 3D grid coordinates (x, y, z) into 2D screen offset.
     * x: along left-to-bottom-right axis
     * y: along right-to-bottom-left axis
     * z: vertical elevation (upwards)
     */
    fun toScreen(
        gridX: Float,
        gridY: Float,
        gridZ: Float = 0f,
        originX: Float = 0f,
        originY: Float = 0f
    ): Offset {
        val screenX = originX + (gridX - gridY) * HALF_TILE_W
        val screenY = originY + (gridX + gridY) * HALF_TILE_H - (gridZ * HEIGHT_SCALE)
        return Offset(screenX, screenY)
    }

    /**
     * Calculate 2D distance between two points
     */
    fun distance(p1: Offset, p2: Offset): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}

package com.example.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Reusable drawing primitives for 2D isometric scenes.
 */
object IsoDrawPrimitives {

    /**
     * Draws an isometric diamond floor tile at (gridX, gridY).
     */
    fun drawFloorTile(
        drawScope: DrawScope,
        gridX: Float,
        gridY: Float,
        originX: Float,
        originY: Float,
        tileColor: Color,
        borderColor: Color = Color(0x33000000),
        highlightColor: Color = Color(0x22FFFFFF)
    ) {
        val top = IsoMath.toScreen(gridX, gridY, 0f, originX, originY)
        val right = IsoMath.toScreen(gridX + 1f, gridY, 0f, originX, originY)
        val bottom = IsoMath.toScreen(gridX + 1f, gridY + 1f, 0f, originX, originY)
        val left = IsoMath.toScreen(gridX, gridY + 1f, 0f, originX, originY)

        val tilePath = Path().apply {
            moveTo(top.x, top.y)
            lineTo(right.x, right.y)
            lineTo(bottom.x, bottom.y)
            lineTo(left.x, left.y)
            close()
        }

        drawScope.drawPath(tilePath, color = tileColor, style = Fill)
        drawScope.drawPath(tilePath, color = borderColor, style = Stroke(width = 1.2f))

        // Subtle highlight line on top edges
        drawScope.drawLine(
            color = highlightColor,
            start = left,
            end = top,
            strokeWidth = 1f
        )
        drawScope.drawLine(
            color = highlightColor,
            start = top,
            end = right,
            strokeWidth = 1f
        )
    }

    /**
     * Draws an isometric 3D box starting from (gx, gy, gz) with dimensions (sizeX, sizeY, heightZ).
     * Includes realistic directional lighting on the three visible faces.
     */
    fun drawIsoBox(
        drawScope: DrawScope,
        gx: Float,
        gy: Float,
        gz: Float,
        sizeX: Float,
        sizeY: Float,
        heightZ: Float,
        originX: Float,
        originY: Float,
        baseColor: Color,
        topColorTint: Color? = null,
        leftColorTint: Color? = null,
        rightColorTint: Color? = null,
        edgeColor: Color? = null
    ) {
        // Calculate 8 corners of the 3D box
        // Bottom 4 vertices (at gz)
        val b0 = IsoMath.toScreen(gx, gy, gz, originX, originY) // top-most in iso
        val b1 = IsoMath.toScreen(gx + sizeX, gy, gz, originX, originY) // right
        val b2 = IsoMath.toScreen(gx + sizeX, gy + sizeY, gz, originX, originY) // bottom
        val b3 = IsoMath.toScreen(gx, gy + sizeY, gz, originX, originY) // left

        // Top 4 vertices (at gz + heightZ)
        val t0 = IsoMath.toScreen(gx, gy, gz + heightZ, originX, originY)
        val t1 = IsoMath.toScreen(gx + sizeX, gy, gz + heightZ, originX, originY)
        val t2 = IsoMath.toScreen(gx + sizeX, gy + sizeY, gz + heightZ, originX, originY)
        val t3 = IsoMath.toScreen(gx, gy + sizeY, gz + heightZ, originX, originY)

        // Shading colors derived from baseColor:
        // Top face receives bright ambient/overhead light
        val topColor = topColorTint ?: Color(
            red = (baseColor.red * 1.15f).coerceIn(0f, 1f),
            green = (baseColor.green * 1.15f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 1.15f).coerceIn(0f, 1f),
            alpha = baseColor.alpha
        )
        // Right face receives medium light
        val rightColor = rightColorTint ?: Color(
            red = (baseColor.red * 0.95f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.95f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.95f).coerceIn(0f, 1f),
            alpha = baseColor.alpha
        )
        // Left face is in shadow
        val leftColor = leftColorTint ?: Color(
            red = (baseColor.red * 0.72f).coerceIn(0f, 1f),
            green = (baseColor.green * 0.72f).coerceIn(0f, 1f),
            blue = (baseColor.blue * 0.72f).coerceIn(0f, 1f),
            alpha = baseColor.alpha
        )

        // Draw Left Face: b3 -> b2 -> t2 -> t3
        val leftFace = Path().apply {
            moveTo(b3.x, b3.y)
            lineTo(b2.x, b2.y)
            lineTo(t2.x, t2.y)
            lineTo(t3.x, t3.y)
            close()
        }
        drawScope.drawPath(leftFace, color = leftColor, style = Fill)

        // Draw Right Face: b1 -> b2 -> t2 -> t1
        val rightFace = Path().apply {
            moveTo(b1.x, b1.y)
            lineTo(b2.x, b2.y)
            lineTo(t2.x, t2.y)
            lineTo(t1.x, t1.y)
            close()
        }
        drawScope.drawPath(rightFace, color = rightColor, style = Fill)

        // Draw Top Face: t0 -> t1 -> t2 -> t3
        val topFace = Path().apply {
            moveTo(t0.x, t0.y)
            lineTo(t1.x, t1.y)
            lineTo(t2.x, t2.y)
            lineTo(t3.x, t3.y)
            close()
        }
        drawScope.drawPath(topFace, color = topColor, style = Fill)

        // Optional edge lines for crispness
        val border = edgeColor ?: Color(0x30000000)
        drawScope.drawPath(leftFace, color = border, style = Stroke(width = 1f))
        drawScope.drawPath(rightFace, color = border, style = Stroke(width = 1f))
        drawScope.drawPath(topFace, color = border, style = Stroke(width = 1f))
    }

    /**
     * Draws an isometric elliptical contact shadow on the floor.
     */
    fun drawIsoShadow(
        drawScope: DrawScope,
        gx: Float,
        gy: Float,
        radiusX: Float,
        radiusY: Float,
        originX: Float,
        originY: Float,
        alpha: Float = 0.35f
    ) {
        val center = IsoMath.toScreen(gx, gy, 0f, originX, originY)
        val shadowPath = Path().apply {
            // Isometric shadow is tilted ellipse
            val rx = radiusX * IsoMath.HALF_TILE_W
            val ry = radiusY * IsoMath.HALF_TILE_H
            addOval(
                androidx.compose.ui.geometry.Rect(
                    center.x - rx,
                    center.y - ry,
                    center.x + rx,
                    center.y + ry
                )
            )
        }
        drawScope.drawPath(shadowPath, color = Color.Black.copy(alpha = alpha), style = Fill)
    }
}

package com.example.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Procedural isometric rendering for the Enchanted Pastel Cottage structure:
 * starlight backdrop, parquet floor, pastel rug, cottage plaster walls,
 * starry arched window, fairy light garlands, and floral oak door.
 */
object PastelCottageArchitecture {

    private val SageTimber = Color(0xFF8AA386)
    private val SageTimberDark = Color(0xFF698265)
    private val HoneyWood = Color(0xFFC28E62)
    private val HoneyWoodDark = Color(0xFF8D5E3A)
    private val PlasterLeft = Color(0xFFE7D3CC)   // Warm blush cream in gentle shadow
    private val PlasterRight = Color(0xFFF6E9E1)  // Sunlit/starlit warm cream plaster
    private val WainscotLeft = Color(0xFFB5C7B2)  // Soft pastel sage paneling
    private val WainscotRight = Color(0xFFC9DBC6)
    private val BrassGold = Color(0xFFE8B94B)
    private val BrassGlow = Color(0xFFFDE68A)

    fun drawStarlightBackdrop(
        drawScope: DrawScope,
        canvasW: Float,
        canvasH: Float,
        twinklePhase: Float
    ) {
        // Subtle 4-point twinkling stars & pastel fireflies around the floating cottage
        val starCoords = listOf(
            Triple(0.10f, 0.12f, 4.5f),
            Triple(0.22f, 0.08f, 3.2f),
            Triple(0.82f, 0.10f, 5.0f),
            Triple(0.90f, 0.22f, 3.6f),
            Triple(0.14f, 0.38f, 3.0f),
            Triple(0.88f, 0.44f, 4.2f),
            Triple(0.18f, 0.80f, 3.8f),
            Triple(0.84f, 0.78f, 4.5f),
            Triple(0.08f, 0.62f, 2.8f),
            Triple(0.92f, 0.64f, 3.1f)
        )
        starCoords.forEachIndexed { idx, (fx, fy, baseSize) ->
            val pulse = 0.55f + 0.45f * kotlin.math.sin((twinklePhase + idx * 0.7f).toDouble()).toFloat()
            val cx = canvasW * fx
            val cy = canvasH * fy
            val r = baseSize * pulse
            val starColor = if (idx % 2 == 0) Color(0xFFFDE68A) else Color(0xFFE9D5FF)

            // Soft halo
            drawScope.drawCircle(
                color = starColor.copy(alpha = 0.18f * pulse),
                radius = r * 2.8f,
                center = Offset(cx, cy)
            )
            // 4-point star sparkle
            drawScope.drawLine(
                color = starColor.copy(alpha = 0.9f * pulse),
                start = Offset(cx - r * 1.4f, cy),
                end = Offset(cx + r * 1.4f, cy),
                strokeWidth = 1.6f,
                cap = StrokeCap.Round
            )
            drawScope.drawLine(
                color = starColor.copy(alpha = 0.9f * pulse),
                start = Offset(cx, cy - r * 1.4f),
                end = Offset(cx, cy + r * 1.4f),
                strokeWidth = 1.6f,
                cap = StrokeCap.Round
            )
        }
    }

    fun drawFloor(
        drawScope: DrawScope,
        gridSize: Int,
        originX: Float,
        originY: Float
    ) {
        val tileWarm = Color(0xFFD6AE8C)
        val tileCream = Color(0xFFE5C4A7)
        val tileBorder = Color(0xFFA37858)

        for (x in 0 until gridSize) {
            for (y in 0 until gridSize) {
                val isAlt = (x + y) % 2 == 0
                IsoDrawPrimitives.drawFloorTile(
                    drawScope = drawScope,
                    gridX = x.toFloat(),
                    gridY = y.toFloat(),
                    originX = originX,
                    originY = originY,
                    tileColor = if (isAlt) tileCream else tileWarm,
                    borderColor = tileBorder,
                    highlightColor = Color(0x33FFFDF9)
                )
            }
        }

        drawPastelStarRug(drawScope, originX, originY)
    }

    private fun drawPastelStarRug(drawScope: DrawScope, originX: Float, originY: Float) {
        val pTop = IsoMath.toScreen(2.2f, 2.2f, 0.02f, originX, originY)
        val pRight = IsoMath.toScreen(4.8f, 2.2f, 0.02f, originX, originY)
        val pBottom = IsoMath.toScreen(4.8f, 4.8f, 0.02f, originX, originY)
        val pLeft = IsoMath.toScreen(2.2f, 4.8f, 0.02f, originX, originY)

        val rugPath = Path().apply {
            moveTo(pTop.x, pTop.y)
            lineTo(pRight.x, pRight.y)
            lineTo(pBottom.x, pBottom.y)
            lineTo(pLeft.x, pLeft.y)
            close()
        }

        // Soft pastel rose & lavender woven rug
        drawScope.drawPath(rugPath, color = Color(0xFFD99AB2), style = Fill)
        drawScope.drawPath(rugPath, color = BrassGold, style = Stroke(width = 3f))

        val ipTop = IsoMath.toScreen(2.5f, 2.5f, 0.03f, originX, originY)
        val ipRight = IsoMath.toScreen(4.5f, 2.5f, 0.03f, originX, originY)
        val ipBottom = IsoMath.toScreen(4.5f, 4.5f, 0.03f, originX, originY)
        val ipLeft = IsoMath.toScreen(2.5f, 4.5f, 0.03f, originX, originY)

        val innerPath = Path().apply {
            moveTo(ipTop.x, ipTop.y)
            lineTo(ipRight.x, ipRight.y)
            lineTo(ipBottom.x, ipBottom.y)
            lineTo(ipLeft.x, ipLeft.y)
            close()
        }
        drawScope.drawPath(innerPath, color = Color(0xFFC2B2EC), style = Fill)
        drawScope.drawPath(innerPath, color = Color(0xFFFDE68A), style = Stroke(width = 1.6f))

        // Starlight medallion in the center
        val center = IsoMath.toScreen(3.5f, 3.5f, 0.04f, originX, originY)
        drawScope.drawCircle(color = Color(0x55FFFBEB), radius = 14f, center = center)
        drawScope.drawCircle(color = BrassGold, radius = 9f, center = center, style = Stroke(width = 1.6f))
        drawScope.drawCircle(color = Color(0xFFFDE68A), radius = 3.5f, center = center)
    }

    fun drawWalls(
        drawScope: DrawScope,
        gridSize: Int,
        wallHeight: Float,
        originX: Float,
        originY: Float,
        twinklePhase: Float
    ) {
        val wainscotHeight = 1.05f

        // --- 1. LEFT WALL (y=0) ---
        val lwB0 = IsoMath.toScreen(0f, 0f, 0f, originX, originY)
        val lwB1 = IsoMath.toScreen(gridSize.toFloat(), 0f, 0f, originX, originY)
        val lwT1 = IsoMath.toScreen(gridSize.toFloat(), 0f, wallHeight, originX, originY)
        val lwT0 = IsoMath.toScreen(0f, 0f, wallHeight, originX, originY)

        val leftWall = Path().apply {
            moveTo(lwB0.x, lwB0.y); lineTo(lwB1.x, lwB1.y)
            lineTo(lwT1.x, lwT1.y); lineTo(lwT0.x, lwT0.y); close()
        }
        drawScope.drawPath(leftWall, color = PlasterLeft, style = Fill)

        // Sage wainscoting lower trim on Left Wall
        val lwW1 = IsoMath.toScreen(gridSize.toFloat(), 0f, wainscotHeight, originX, originY)
        val lwW0 = IsoMath.toScreen(0f, 0f, wainscotHeight, originX, originY)
        val leftWainscot = Path().apply {
            moveTo(lwB0.x, lwB0.y); lineTo(lwB1.x, lwB1.y)
            lineTo(lwW1.x, lwW1.y); lineTo(lwW0.x, lwW0.y); close()
        }
        drawScope.drawPath(leftWainscot, color = WainscotLeft, style = Fill)
        drawScope.drawLine(color = SageTimberDark, start = lwW0, end = lwW1, strokeWidth = 3f)

        // Timber beams on Left Wall
        drawScope.drawLine(color = SageTimberDark, start = lwT0, end = lwT1, strokeWidth = 6f)
        drawScope.drawLine(color = HoneyWoodDark, start = lwB0, end = lwB1, strokeWidth = 3.5f)

        // --- 2. RIGHT WALL (x=0) ---
        val rwB0 = IsoMath.toScreen(0f, 0f, 0f, originX, originY)
        val rwB1 = IsoMath.toScreen(0f, gridSize.toFloat(), 0f, originX, originY)
        val rwT1 = IsoMath.toScreen(0f, gridSize.toFloat(), wallHeight, originX, originY)
        val rwT0 = IsoMath.toScreen(0f, 0f, wallHeight, originX, originY)

        val rightWall = Path().apply {
            moveTo(rwB0.x, rwB0.y); lineTo(rwB1.x, rwB1.y)
            lineTo(rwT1.x, rwT1.y); lineTo(rwT0.x, rwT0.y); close()
        }
        drawScope.drawPath(rightWall, color = PlasterRight, style = Fill)

        // Sage wainscoting on Right Wall
        val rwW1 = IsoMath.toScreen(0f, gridSize.toFloat(), wainscotHeight, originX, originY)
        val rwW0 = IsoMath.toScreen(0f, 0f, wainscotHeight, originX, originY)
        val rightWainscot = Path().apply {
            moveTo(rwB0.x, rwB0.y); lineTo(rwB1.x, rwB1.y)
            lineTo(rwW1.x, rwW1.y); lineTo(rwW0.x, rwW0.y); close()
        }
        drawScope.drawPath(rightWainscot, color = WainscotRight, style = Fill)
        drawScope.drawLine(color = SageTimber, start = rwW0, end = rwW1, strokeWidth = 3f)

        // Timber top & base beams on Right Wall
        drawScope.drawLine(color = SageTimber, start = rwT0, end = rwT1, strokeWidth = 6f)
        drawScope.drawLine(color = HoneyWood, start = rwB0, end = rwB1, strokeWidth = 3.5f)

        // Corner Cottage Pillar at (0, 0)
        drawScope.drawLine(color = HoneyWoodDark, start = lwB0, end = lwT0, strokeWidth = 7f)

        // --- 3. STARLIGHT COTTAGE WINDOW ON RIGHT WALL ---
        drawStarlightWindow(drawScope, originX, originY, twinklePhase)

        // --- 4. FAIRY LIGHT GARLANDS ALONG BOTH UPPER BEAMS ---
        drawFairyLightGarland(drawScope, originX, originY, isLeftWall = true, gridSize = gridSize, wallHeight = wallHeight, twinklePhase = twinklePhase)
        drawFairyLightGarland(drawScope, originX, originY, isLeftWall = false, gridSize = gridSize, wallHeight = wallHeight, twinklePhase = twinklePhase)

        // --- 5. WARM LANTERN SCONCE ---
        drawCottageLantern(drawScope, originX, originY, gy = 1.0f, gz = 2.5f)
    }

    private fun drawStarlightWindow(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        twinklePhase: Float
    ) {
        val wyStart = 1.65f
        val wyEnd = 2.75f
        val wzBottom = 1.45f
        val wzTop = 2.95f

        val pBL = IsoMath.toScreen(0.01f, wyStart, wzBottom, originX, originY)
        val pBR = IsoMath.toScreen(0.01f, wyEnd, wzBottom, originX, originY)
        val pTR = IsoMath.toScreen(0.01f, wyEnd, wzTop, originX, originY)
        val pTL = IsoMath.toScreen(0.01f, wyStart, wzTop, originX, originY)

        val windowPath = Path().apply {
            moveTo(pBL.x, pBL.y)
            lineTo(pBR.x, pBR.y)
            lineTo(pTR.x, pTR.y)
            val archPeak = IsoMath.toScreen(0.01f, (wyStart + wyEnd) / 2f, wzTop + 0.35f, originX, originY)
            quadraticTo(archPeak.x, archPeak.y, pTL.x, pTL.y)
            close()
        }

        // Midnight starry sky inside the cottage window
        drawScope.drawPath(windowPath, color = Color(0xFF23184A), style = Fill)

        // Stars & golden crescent moon inside window
        val winCenter = IsoMath.toScreen(0.02f, (wyStart + wyEnd) / 2f, (wzBottom + wzTop) / 2f, originX, originY)
        val pulse = 0.6f + 0.4f * kotlin.math.sin(twinklePhase.toDouble()).toFloat()
        drawScope.drawCircle(color = Color(0xFFFDE68A), radius = 4.8f, center = Offset(winCenter.x + 6f, winCenter.y - 8f))
        drawScope.drawCircle(color = Color(0xFF23184A), radius = 3.8f, center = Offset(winCenter.x + 8.5f, winCenter.y - 9.5f))
        drawScope.drawCircle(color = Color(0xFFE9D5FF).copy(alpha = pulse), radius = 1.8f, center = Offset(winCenter.x - 8f, winCenter.y - 4f))
        drawScope.drawCircle(color = Color(0xFFFDE68A).copy(alpha = pulse), radius = 1.5f, center = Offset(winCenter.x - 3f, winCenter.y + 8f))

        // Mullion crossbars & cream cottage window frame
        val midVBottom = IsoMath.toScreen(0.02f, (wyStart + wyEnd) / 2f, wzBottom, originX, originY)
        val midVTop = IsoMath.toScreen(0.02f, (wyStart + wyEnd) / 2f, wzTop + 0.14f, originX, originY)
        val midHLeft = IsoMath.toScreen(0.02f, wyStart, (wzBottom + wzTop) / 2f, originX, originY)
        val midHRight = IsoMath.toScreen(0.02f, wyEnd, (wzBottom + wzTop) / 2f, originX, originY)

        drawScope.drawLine(color = Color(0xFFFDF6EE), start = midVBottom, end = midVTop, strokeWidth = 2f)
        drawScope.drawLine(color = Color(0xFFFDF6EE), start = midHLeft, end = midHRight, strokeWidth = 2f)
        drawScope.drawPath(windowPath, color = HoneyWood, style = Stroke(width = 3.5f))

        // Flower window box beneath the sill
        IsoDrawPrimitives.drawIsoBox(
            drawScope = drawScope,
            gx = 0.01f,
            gy = wyStart - 0.05f,
            gz = wzBottom - 0.25f,
            sizeX = 0.18f,
            sizeY = (wyEnd - wyStart) + 0.1f,
            heightZ = 0.22f,
            originX = originX,
            originY = originY,
            baseColor = HoneyWood
        )
        // Little pastel blooms in the window box
        val flowerColors = listOf(Color(0xFFFB7185), Color(0xFFFDE047), Color(0xFFC084FC), Color(0xFFF472B6))
        flowerColors.forEachIndexed { idx, col ->
            val fy = wyStart + 0.15f + idx * 0.25f
            val fPos = IsoMath.toScreen(0.12f, fy, wzBottom, originX, originY)
            drawScope.drawCircle(color = Color(0xFF4ADE80), radius = 4f, center = Offset(fPos.x, fPos.y - 2f))
            drawScope.drawCircle(color = col, radius = 3f, center = Offset(fPos.x, fPos.y - 4f))
        }
    }

    private fun drawFairyLightGarland(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        isLeftWall: Boolean,
        gridSize: Int,
        wallHeight: Float,
        twinklePhase: Float
    ) {
        val bulbColors = listOf(Color(0xFFFDE68A), Color(0xFFF9A8D4), Color(0xFFA5F3FC), Color(0xFFD8B4FE))
        val count = 6
        for (i in 1..count) {
            val t = i.toFloat() / (count + 1)
            val posCoord = t * gridSize
            val sag = kotlin.math.sin(t * Math.PI).toFloat() * 0.18f
            val gz = wallHeight - 0.22f - sag
            val pos = if (isLeftWall) {
                IsoMath.toScreen(posCoord, 0.04f, gz, originX, originY)
            } else {
                IsoMath.toScreen(0.04f, posCoord, gz, originX, originY)
            }

            val glowCol = bulbColors[i % bulbColors.size]
            val pulse = 0.65f + 0.35f * kotlin.math.sin((twinklePhase + i).toDouble()).toFloat()

            drawScope.drawCircle(
                color = glowCol.copy(alpha = 0.25f * pulse),
                radius = 9f,
                center = pos
            )
            drawScope.drawCircle(
                color = glowCol,
                radius = 3f,
                center = pos
            )
        }
    }

    private fun drawCottageLantern(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gy: Float,
        gz: Float
    ) {
        val pos = IsoMath.toScreen(0.02f, gy, gz, originX, originY)
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x88FDE68A), Color(0x33FBBF24), Color(0x00FBBF24)),
                center = pos,
                radius = 52f
            ),
            radius = 52f,
            center = pos
        )
        drawScope.drawLine(color = BrassGold, start = pos, end = Offset(pos.x + 8f, pos.y - 3f), strokeWidth = 2.5f)
        drawScope.drawCircle(color = BrassGlow, radius = 4.5f, center = Offset(pos.x + 8f, pos.y - 6f))
    }

    fun drawEntranceDoor(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        doorXStart: Float = 3.6f,
        doorXEnd: Float = 5.2f,
        doorHeight: Float = 3.0f
    ) {
        val bStart = IsoMath.toScreen(doorXStart, 0.01f, 0f, originX, originY)
        val bEnd = IsoMath.toScreen(doorXEnd, 0.01f, 0f, originX, originY)
        val tEnd = IsoMath.toScreen(doorXEnd, 0.01f, doorHeight, originX, originY)
        val tStart = IsoMath.toScreen(doorXStart, 0.01f, doorHeight, originX, originY)

        // Warm golden twilight glow peeking under the cottage sill
        val sillGlow = Path().apply {
            val g1 = IsoMath.toScreen(doorXStart - 0.15f, 0.1f, 0f, originX, originY)
            val g2 = IsoMath.toScreen(doorXEnd + 0.15f, 0.1f, 0f, originX, originY)
            val g3 = IsoMath.toScreen(doorXEnd + 0.25f, 0.5f, 0f, originX, originY)
            val g4 = IsoMath.toScreen(doorXStart - 0.05f, 0.5f, 0f, originX, originY)
            moveTo(g1.x, g1.y); lineTo(g2.x, g2.y); lineTo(g3.x, g3.y); lineTo(g4.x, g4.y); close()
        }
        drawScope.drawPath(sillGlow, color = Color(0x55FDE68A), style = Fill)

        // Honey wood frame
        val framePath = Path().apply {
            moveTo(bStart.x - 3f, bStart.y); lineTo(bEnd.x + 3f, bEnd.y)
            lineTo(tEnd.x + 3f, tEnd.y); lineTo(tStart.x - 3f, tStart.y); close()
        }
        drawScope.drawPath(framePath, color = HoneyWood, style = Stroke(width = 5f))

        // Arched pastel sage cottage door
        val doorBody = Path().apply {
            moveTo(bStart.x, bStart.y)
            lineTo(bEnd.x, bEnd.y)
            lineTo(tEnd.x, tEnd.y)
            val midTop = IsoMath.toScreen((doorXStart + doorXEnd) / 2f, 0.01f, doorHeight + 0.3f, originX, originY)
            quadraticTo(midTop.x, midTop.y, tStart.x, tStart.y)
            close()
        }
        drawScope.drawPath(doorBody, color = SageTimber, style = Fill)
        drawScope.drawPath(doorBody, color = SageTimberDark, style = Stroke(width = 2f))

        // Vertical planks
        for (i in 1 until 4) {
            val gx = doorXStart + (doorXEnd - doorXStart) * (i.toFloat() / 4)
            drawScope.drawLine(
                color = SageTimberDark,
                start = IsoMath.toScreen(gx, 0.015f, 0f, originX, originY),
                end = IsoMath.toScreen(gx, 0.015f, doorHeight, originX, originY),
                strokeWidth = 1.3f
            )
        }

        // Brass strap hinges
        for (z in listOf(doorHeight * 0.28f, doorHeight * 0.72f)) {
            drawScope.drawLine(
                color = BrassGold,
                start = IsoMath.toScreen(doorXStart + 0.1f, 0.02f, z, originX, originY),
                end = IsoMath.toScreen(doorXEnd - 0.25f, 0.02f, z, originX, originY),
                strokeWidth = 2.8f
            )
        }

        // Floral Cottage Wreath on upper door
        val wreathPos = IsoMath.toScreen((doorXStart + doorXEnd) / 2f, 0.03f, doorHeight * 0.65f, originX, originY)
        drawScope.drawCircle(color = Color(0xFF4ADE80), radius = 10f, center = wreathPos, style = Stroke(width = 3.5f))
        drawScope.drawCircle(color = Color(0xFFFB7185), radius = 2.5f, center = Offset(wreathPos.x - 6f, wreathPos.y - 5f))
        drawScope.drawCircle(color = Color(0xFFFDE68A), radius = 2.5f, center = Offset(wreathPos.x + 6f, wreathPos.y + 4f))
        drawScope.drawCircle(color = Color(0xFFC4B5FD), radius = 2.5f, center = Offset(wreathPos.x + 2f, wreathPos.y - 8f))

        // Brass door knob
        val handlePos = IsoMath.toScreen(doorXEnd - 0.32f, 0.025f, doorHeight * 0.45f, originX, originY)
        drawScope.drawCircle(color = BrassGold, radius = 3.5f, center = handlePos)
    }
}

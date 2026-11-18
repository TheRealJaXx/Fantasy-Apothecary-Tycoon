package com.example.ui.render

import androidx.compose.ui.geometry.CornerRadius
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
 * Procedural isometric rendering for Enchanted Pastel Cottage furniture:
 * ribbon-trimmed sales counter with Crystal Ball, botanical shelves,
 * potion flasks, and the shimmering brass-and-crystal cauldron.
 */
object PastelCottageFurniture {

    private val HoneyWood = Color(0xFFC28E62)
    private val HoneyWoodLight = Color(0xFFD8A97F)
    private val HoneyWoodDark = Color(0xFF8C5D38)
    private val CreamPanel = Color(0xFFF7ECE3)
    private val SageTrim = Color(0xFF93AE8F)
    private val BrassGold = Color(0xFFE8B94B)
    private val BrassHighlight = Color(0xFFFEF08A)

    enum class BottleShape {
        ROUND,
        FLASK,
        VIAL
    }

    fun drawSalesCounter(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float = 3.5f,
        gy: Float = 1.6f,
        sizeX: Float = 2.0f,
        sizeY: Float = 0.85f,
        heightZ: Float = 1.15f
    ) {
        IsoDrawPrimitives.drawIsoShadow(
            drawScope = drawScope,
            gx = gx + sizeX * 0.5f,
            gy = gy + sizeY * 0.5f,
            radiusX = sizeX * 1.05f,
            radiusY = sizeY * 1.1f,
            originX = originX,
            originY = originY,
            alpha = 0.35f
        )

        // Honey-oak counter body
        IsoDrawPrimitives.drawIsoBox(
            drawScope = drawScope,
            gx = gx,
            gy = gy,
            gz = 0f,
            sizeX = sizeX,
            sizeY = sizeY,
            heightZ = heightZ,
            originX = originX,
            originY = originY,
            baseColor = HoneyWood,
            topColorTint = HoneyWoodLight,
            leftColorTint = HoneyWoodDark,
            rightColorTint = HoneyWood
        )

        // Cream recessed decorative front panels
        val panelGZ = 0.18f
        val panelHeight = heightZ - 0.36f
        val segments = listOf(
            (gx + 0.15f) to (gx + sizeX * 0.5f - 0.08f),
            (gx + sizeX * 0.5f + 0.08f) to (gx + sizeX - 0.15f)
        )
        segments.forEach { (startX, endX) ->
            val p1 = IsoMath.toScreen(startX, gy + sizeY + 0.01f, panelGZ, originX, originY)
            val p2 = IsoMath.toScreen(endX, gy + sizeY + 0.01f, panelGZ, originX, originY)
            val p3 = IsoMath.toScreen(endX, gy + sizeY + 0.01f, panelGZ + panelHeight, originX, originY)
            val p4 = IsoMath.toScreen(startX, gy + sizeY + 0.01f, panelGZ + panelHeight, originX, originY)
            val path = Path().apply {
                moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close()
            }
            drawScope.drawPath(path, color = CreamPanel, style = Fill)
            drawScope.drawPath(path, color = BrassGold, style = Stroke(width = 1.2f))
        }

        // Pastel lavender lace runner draped across middle of countertop
        val r1 = IsoMath.toScreen(gx + 0.72f, gy + 0.05f, heightZ + 0.01f, originX, originY)
        val r2 = IsoMath.toScreen(gx + 1.22f, gy + 0.05f, heightZ + 0.01f, originX, originY)
        val r3 = IsoMath.toScreen(gx + 1.22f, gy + sizeY, heightZ + 0.01f, originX, originY)
        val r4 = IsoMath.toScreen(gx + 0.72f, gy + sizeY, heightZ + 0.01f, originX, originY)
        val runner = Path().apply {
            moveTo(r1.x, r1.y); lineTo(r2.x, r2.y); lineTo(r3.x, r3.y); lineTo(r4.x, r4.y); close()
        }
        drawScope.drawPath(runner, color = Color(0xFFD8B4FE), style = Fill)

        // 1. Glowing Crystal Ball on velvet cushion (for Morning Forecast!)
        drawCounterCrystalBall(drawScope, originX, originY, gx + 0.38f, gy + 0.35f, heightZ)

        // 2. Open Starlight Ledger on lace runner
        drawStarlightLedger(drawScope, originX, originY, gx + 0.97f, gy + 0.42f, heightZ)

        // 3. Brass service bell & pastel showcase potions
        drawCounterBell(drawScope, originX, originY, gx + 1.42f, gy + 0.35f, heightZ)
        drawPotionBottle(drawScope, originX, originY, gx + 1.65f, gy + 0.48f, heightZ, Color(0xFFF43F5E), BottleShape.ROUND, 0.85f)
        drawPotionBottle(drawScope, originX, originY, gx + 1.82f, gy + 0.25f, heightZ, Color(0xFF38BDF8), BottleShape.FLASK, 0.85f)
    }

    private fun drawCounterCrystalBall(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float
    ) {
        val pos = IsoMath.toScreen(gx, gy, gz, originX, originY)
        // Gold stand
        drawScope.drawOval(
            color = BrassGold,
            topLeft = Offset(pos.x - 8f, pos.y - 3f),
            size = Size(16f, 6f)
        )
        val orbCenter = Offset(pos.x, pos.y - 11f)
        // Outer aura
        drawScope.drawCircle(
            color = Color(0x44C4B5FD),
            radius = 13f,
            center = orbCenter
        )
        // Iridescent crystal sphere
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFC4B5FD), Color(0xFF7C3AED)),
                center = Offset(orbCenter.x - 3f, orbCenter.y - 3f),
                radius = 10f
            ),
            radius = 9f,
            center = orbCenter
        )
    }

    private fun drawCounterBell(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float
    ) {
        val pos = IsoMath.toScreen(gx, gy, gz, originX, originY)
        drawScope.drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BrassHighlight, BrassGold, Color(0xFF9A6B16)),
                center = Offset(pos.x - 1f, pos.y - 4f),
                radius = 6f
            ),
            radius = 5.5f,
            center = Offset(pos.x, pos.y - 3f)
        )
    }

    private fun drawStarlightLedger(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float
    ) {
        val p1 = IsoMath.toScreen(gx - 0.16f, gy - 0.12f, gz + 0.02f, originX, originY)
        val p2 = IsoMath.toScreen(gx + 0.18f, gy - 0.12f, gz + 0.02f, originX, originY)
        val p3 = IsoMath.toScreen(gx + 0.18f, gy + 0.16f, gz + 0.02f, originX, originY)
        val p4 = IsoMath.toScreen(gx - 0.16f, gy + 0.16f, gz + 0.02f, originX, originY)

        val pages = Path().apply {
            moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); lineTo(p4.x, p4.y); close()
        }
        drawScope.drawPath(pages, color = Color(0xFFFFFBEB), style = Fill)
        drawScope.drawPath(pages, color = Color(0xFF9333EA), style = Stroke(width = 1.2f))
    }

    fun drawIngredientShelves(
        drawScope: DrawScope,
        originX: Float,
        originY: Float
    ) {
        // --- SHELF 1: Tall sage-and-oak botanical bookcase on Right Wall ---
        val s1X = 0.15f
        val s1Y = 3.05f
        val s1W = 0.55f
        val s1D = 2.0f
        val s1H = 2.85f

        IsoDrawPrimitives.drawIsoShadow(
            drawScope = drawScope,
            gx = s1X + s1W * 0.5f,
            gy = s1Y + s1D * 0.5f,
            radiusX = s1W * 1.1f,
            radiusY = s1D * 1.05f,
            originX = originX,
            originY = originY,
            alpha = 0.3f
        )

        IsoDrawPrimitives.drawIsoBox(
            drawScope = drawScope,
            gx = s1X,
            gy = s1Y,
            gz = 0f,
            sizeX = s1W,
            sizeY = s1D,
            heightZ = s1H,
            originX = originX,
            originY = originY,
            baseColor = SageTrim,
            topColorTint = HoneyWoodLight,
            leftColorTint = Color(0xFF5D765A),
            rightColorTint = HoneyWood
        )

        for (tier in 1..2) {
            val tierZ = (s1H / 3f) * tier
            IsoDrawPrimitives.drawIsoBox(
                drawScope = drawScope,
                gx = s1X,
                gy = s1Y + 0.05f,
                gz = tierZ,
                sizeX = s1W,
                sizeY = s1D - 0.1f,
                heightZ = 0.08f,
                originX = originX,
                originY = originY,
                baseColor = HoneyWoodLight
            )
        }

        val jarZ = (s1H / 3f) * 2f + 0.1f
        drawIngredientJar(drawScope, originX, originY, s1X + 0.25f, s1Y + 0.35f, jarZ, Color(0xFFFB7185))
        drawIngredientJar(drawScope, originX, originY, s1X + 0.25f, s1Y + 0.80f, jarZ, Color(0xFF60A5FA))
        drawIngredientJar(drawScope, originX, originY, s1X + 0.25f, s1Y + 1.25f, jarZ, Color(0xFF4ADE80))
        drawIngredientJar(drawScope, originX, originY, s1X + 0.25f, s1Y + 1.65f, jarZ, Color(0xFFFDE047))

        val bookZ = (s1H / 3f) + 0.08f
        drawPastelBook(drawScope, originX, originY, s1X + 0.2f, s1Y + 0.45f, bookZ, Color(0xFFBE185D))
        drawPastelBook(drawScope, originX, originY, s1X + 0.2f, s1Y + 0.78f, bookZ, Color(0xFF7C3AED))
        drawPastelBook(drawScope, originX, originY, s1X + 0.2f, s1Y + 1.12f, bookZ, Color(0xFF0F766E))

        // --- SHELF 2: Herbal Wall Rack on Left Wall ---
        val s2X = 1.3f
        val s2Y = 0.1f
        val s2W = 1.8f
        val s2D = 0.45f
        val s2Z = 1.8f
        val s2H = 1.05f

        IsoDrawPrimitives.drawIsoBox(
            drawScope = drawScope,
            gx = s2X,
            gy = s2Y,
            gz = s2Z,
            sizeX = s2W,
            sizeY = s2D,
            heightZ = s2H,
            originX = originX,
            originY = originY,
            baseColor = HoneyWood,
            topColorTint = HoneyWoodLight,
            leftColorTint = HoneyWoodDark,
            rightColorTint = HoneyWood
        )

        drawPotionBottle(drawScope, originX, originY, s2X + 0.35f, s2Y + 0.2f, s2Z + s2H, Color(0xFFF43F5E), BottleShape.VIAL, 0.75f)
        drawPotionBottle(drawScope, originX, originY, s2X + 0.75f, s2Y + 0.2f, s2Z + s2H, Color(0xFFC084FC), BottleShape.ROUND, 0.75f)
        drawPotionBottle(drawScope, originX, originY, s2X + 1.15f, s2Y + 0.2f, s2Z + s2H, Color(0xFF34D399), BottleShape.FLASK, 0.75f)
        drawPotionBottle(drawScope, originX, originY, s2X + 1.50f, s2Y + 0.2f, s2Z + s2H, Color(0xFF38BDF8), BottleShape.VIAL, 0.75f)

        // Hanging herb bundles underneath
        drawHangingHerbBundle(drawScope, originX, originY, s2X + 0.4f, s2Y + s2D + 0.02f, s2Z, Color(0xFF4ADE80))
        drawHangingHerbBundle(drawScope, originX, originY, s2X + 0.9f, s2Y + s2D + 0.02f, s2Z, Color(0xFFC4B5FD))
        drawHangingHerbBundle(drawScope, originX, originY, s2X + 1.4f, s2Y + s2D + 0.02f, s2Z, Color(0xFFFB7185))
    }

    private fun drawIngredientJar(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float,
        contentColor: Color
    ) {
        val center = IsoMath.toScreen(gx, gy, gz, originX, originY)
        drawScope.drawRoundRect(
            color = Color(0x44FFFFFF),
            topLeft = Offset(center.x - 5f, center.y - 12f),
            size = Size(10f, 12f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawScope.drawRoundRect(
            color = contentColor,
            topLeft = Offset(center.x - 4f, center.y - 8f),
            size = Size(8f, 7f),
            cornerRadius = CornerRadius(1.5f, 1.5f)
        )
        drawScope.drawRoundRect(
            color = HoneyWoodDark,
            topLeft = Offset(center.x - 3.5f, center.y - 15f),
            size = Size(7f, 3.5f),
            cornerRadius = CornerRadius(1f, 1f)
        )
    }

    private fun drawPastelBook(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float,
        bookColor: Color
    ) {
        val center = IsoMath.toScreen(gx, gy, gz, originX, originY)
        drawScope.drawRoundRect(
            color = bookColor,
            topLeft = Offset(center.x - 3.5f, center.y - 16f),
            size = Size(7f, 16f),
            cornerRadius = CornerRadius(1.5f, 1.5f)
        )
        drawScope.drawLine(
            color = BrassHighlight,
            start = Offset(center.x - 3f, center.y - 12f),
            end = Offset(center.x + 3f, center.y - 12f),
            strokeWidth = 1f
        )
    }

    private fun drawHangingHerbBundle(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float,
        herbColor: Color
    ) {
        val top = IsoMath.toScreen(gx, gy, gz, originX, originY)
        drawScope.drawLine(color = BrassGold, start = top, end = Offset(top.x, top.y + 4f), strokeWidth = 1.2f)
        val herbPath = Path().apply {
            moveTo(top.x, top.y + 4f)
            lineTo(top.x - 4.5f, top.y + 14f)
            lineTo(top.x + 4.5f, top.y + 14f)
            close()
        }
        drawScope.drawPath(herbPath, color = herbColor, style = Fill)
    }

    /**
     * Draws the Enchanted Crystal & Brass Cauldron on a pastel cottage stone hearth.
     */
    fun drawStarlightCauldron(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float = 1.3f,
        gy: Float = 4.6f,
        brewLiquidColor: Color = Color(0xFFF43F5E),
        twinklePhase: Float = 0f
    ) {
        val baseCenter = IsoMath.toScreen(gx, gy, 0f, originX, originY)

        IsoDrawPrimitives.drawIsoShadow(
            drawScope = drawScope,
            gx = gx,
            gy = gy,
            radiusX = 1.3f,
            radiusY = 1.3f,
            originX = originX,
            originY = originY,
            alpha = 0.35f
        )

        // Pastel cottage cobblestone hearth ring
        val hearthRadiusX = IsoMath.HALF_TILE_W * 0.95f
        val hearthRadiusY = IsoMath.HALF_TILE_H * 0.95f
        val hearthBase = Path().apply {
            addOval(
                Rect(
                    baseCenter.x - hearthRadiusX,
                    baseCenter.y - hearthRadiusY,
                    baseCenter.x + hearthRadiusX,
                    baseCenter.y + hearthRadiusY
                )
            )
        }
        drawScope.drawPath(hearthBase, color = Color(0xFFD6CEC8), style = Fill)
        drawScope.drawPath(hearthBase, color = Color(0xFF9C918A), style = Stroke(width = 2.2f))

        // Warm pastel hearth fire glow
        val emberGlow = Brush.radialGradient(
            colors = listOf(Color(0xFFFDE68A), Color(0xFFFB923C), Color(0x00FB923C)),
            center = Offset(baseCenter.x, baseCenter.y - 4f),
            radius = 30f
        )
        drawScope.drawCircle(brush = emberGlow, radius = 28f, center = Offset(baseCenter.x, baseCenter.y - 4f))

        // Cauldron body (deep starlight plum-brass enchanted vessel)
        val cauldronCenter = IsoMath.toScreen(gx, gy, 0.45f, originX, originY)
        val bodyRadius = 22f

        // Brass legs
        drawScope.drawLine(BrassGold, Offset(cauldronCenter.x - 12f, cauldronCenter.y + 6f), Offset(cauldronCenter.x - 15f, baseCenter.y - 3f), 3f)
        drawScope.drawLine(BrassGold, Offset(cauldronCenter.x + 12f, cauldronCenter.y + 6f), Offset(cauldronCenter.x + 15f, baseCenter.y - 3f), 3f)
        drawScope.drawLine(BrassGold, Offset(cauldronCenter.x, cauldronCenter.y + 9f), baseCenter, 3f)

        val bodyBrush = Brush.radialGradient(
            colors = listOf(Color(0xFF6D59A6), Color(0xFF3E2D6B), Color(0xFF261A47)),
            center = Offset(cauldronCenter.x - 6f, cauldronCenter.y - 4f),
            radius = bodyRadius * 1.3f
        )
        drawScope.drawCircle(brush = bodyBrush, radius = bodyRadius, center = cauldronCenter)

        // Brass rim
        val rimCenter = Offset(cauldronCenter.x, cauldronCenter.y - 14f)
        val rimRx = 20f
        val rimRy = 9f
        drawScope.drawOval(
            color = BrassGold,
            topLeft = Offset(rimCenter.x - rimRx, rimCenter.y - rimRy),
            size = Size(rimRx * 2f, rimRy * 2f)
        )
        drawScope.drawOval(
            color = Color(0xFF8C5D12),
            topLeft = Offset(rimCenter.x - rimRx, rimCenter.y - rimRy),
            size = Size(rimRx * 2f, rimRy * 2f),
            style = Stroke(width = 1.8f)
        )

        // Shimmering dynamic potion brew inside
        val foamColor = Color(
            red = (brewLiquidColor.red * 1.35f).coerceIn(0f, 1f),
            green = (brewLiquidColor.green * 1.35f).coerceIn(0f, 1f),
            blue = (brewLiquidColor.blue * 1.35f).coerceIn(0f, 1f),
            alpha = 1f
        )
        val liquidBrush = Brush.radialGradient(
            colors = listOf(foamColor, brewLiquidColor, brewLiquidColor.copy(alpha = 0.85f)),
            center = rimCenter,
            radius = rimRx
        )
        drawScope.drawOval(
            brush = liquidBrush,
            topLeft = Offset(rimCenter.x - (rimRx - 2.5f), rimCenter.y - (rimRy - 2.5f)),
            size = Size((rimRx - 2.5f) * 2f, (rimRy - 2.5f) * 2f)
        )

        // Twinkling starlight bubbles rising from cauldron
        val bob = kotlin.math.sin(twinklePhase.toDouble()).toFloat() * 2.5f
        drawScope.drawCircle(color = Color(0xDDFFFFFF), radius = 3f, center = Offset(rimCenter.x - 6f, rimCenter.y - 1f))
        drawScope.drawCircle(color = Color(0xDDFFFFFF), radius = 2.4f, center = Offset(rimCenter.x + 6f, rimCenter.y + 1f))
        drawScope.drawCircle(color = BrassHighlight.copy(alpha = 0.7f), radius = 3.5f, center = Offset(rimCenter.x - 2f, rimCenter.y - 18f + bob))
        drawScope.drawCircle(color = Color(0x55FFFFFF), radius = 5f, center = Offset(rimCenter.x + 5f, rimCenter.y - 26f - bob))

        // Brass handles & wooden stirring wand/paddle
        drawScope.drawCircle(color = BrassGold, radius = 4.5f, center = Offset(cauldronCenter.x - bodyRadius, cauldronCenter.y - 6f), style = Stroke(width = 2.2f))
        drawScope.drawCircle(color = BrassGold, radius = 4.5f, center = Offset(cauldronCenter.x + bodyRadius, cauldronCenter.y - 6f), style = Stroke(width = 2.2f))

        drawScope.drawLine(
            color = HoneyWoodLight,
            start = Offset(cauldronCenter.x - 16f, cauldronCenter.y + 12f),
            end = Offset(cauldronCenter.x + 12f, cauldronCenter.y - 28f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    }

    fun drawPotionBottle(
        drawScope: DrawScope,
        originX: Float,
        originY: Float,
        gx: Float,
        gy: Float,
        gz: Float,
        liquidColor: Color,
        bottleShape: BottleShape = BottleShape.ROUND,
        scale: Float = 1.0f
    ) {
        val basePos = IsoMath.toScreen(gx, gy, gz, originX, originY)
        when (bottleShape) {
            BottleShape.ROUND -> {
                val r = 8.5f * scale
                val center = Offset(basePos.x, basePos.y - r)
                drawScope.drawCircle(color = liquidColor, radius = r, center = center)
                drawScope.drawCircle(color = Color(0x88FFFFFF), radius = r * 0.35f, center = Offset(center.x - 2f * scale, center.y - 2f * scale))
                drawScope.drawRoundRect(
                    color = BrassGold,
                    topLeft = Offset(center.x - 2.5f * scale, center.y - r - 4f * scale),
                    size = Size(5f * scale, 4f * scale),
                    cornerRadius = CornerRadius(1f, 1f)
                )
            }
            BottleShape.FLASK -> {
                val h = 17f * scale
                val halfW = 8.5f * scale
                val neckW = 3f * scale
                val path = Path().apply {
                    moveTo(basePos.x - halfW, basePos.y)
                    lineTo(basePos.x + halfW, basePos.y)
                    lineTo(basePos.x + neckW, basePos.y - (h - 5f * scale))
                    lineTo(basePos.x - neckW, basePos.y - (h - 5f * scale))
                    close()
                }
                drawScope.drawPath(path, color = liquidColor, style = Fill)
                drawScope.drawRect(
                    color = BrassGold,
                    topLeft = Offset(basePos.x - neckW, basePos.y - h),
                    size = Size(neckW * 2f, 4f * scale)
                )
            }
            BottleShape.VIAL -> {
                val w = 8f * scale
                val h = 18f * scale
                drawScope.drawRoundRect(
                    color = liquidColor,
                    topLeft = Offset(basePos.x - w / 2f, basePos.y - h),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(w / 2f, w / 2f)
                )
                drawScope.drawRoundRect(
                    color = BrassGold,
                    topLeft = Offset(basePos.x - 2.5f * scale, basePos.y - h - 3.5f * scale),
                    size = Size(5f * scale, 3.5f * scale),
                    cornerRadius = CornerRadius(1f, 1f)
                )
            }
        }
    }
}

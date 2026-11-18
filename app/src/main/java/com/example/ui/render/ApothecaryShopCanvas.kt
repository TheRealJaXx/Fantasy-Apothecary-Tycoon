package com.example.ui.render

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Customer

@Composable
fun ApothecaryShopCanvas(
    onInspectObject: (name: String, description: String) -> Unit,
    currentCustomer: Customer? = null,
    onTapCustomer: () -> Unit = {},
    cauldronColorHex: Long = 0xFFF43F5E,
    onTapCauldron: () -> Unit = {},
    onTapCrystalBall: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val gridSize = 6
    val wallHeight = 3.6f
    val virtualWidth = 520f
    val virtualHeight = 440f

    // Gentle starlight twinkle animation
    val infiniteTransition = rememberInfiniteTransition(label = "starlight")
    val twinklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinklePhase"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3E2D6B),
                        Color(0xFF2B2050),
                        Color(0xFF1F163B)
                    )
                )
            )
            .testTag("apothecary_shop_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentCustomer) {
                    detectTapGestures { tapOffset ->
                        val canvasW = size.width.toFloat()
                        val canvasH = size.height.toFloat()

                        val scale = (kotlin.math.min(canvasW / virtualWidth, canvasH / virtualHeight) * 0.92f)
                            .coerceIn(0.4f, 2.5f)

                        val originX = canvasW / 2f
                        val originY = (canvasH / 2f) - (gridSize * IsoMath.HALF_TILE_H * scale * 0.25f)

                        val localX = (tapOffset.x - originX) / scale
                        val localY = (tapOffset.y - originY) / scale
                        val unscaledTap = Offset(localX, localY)

                        // 1. Check customer or speech bubble
                        if (currentCustomer != null) {
                            val custBasePos = IsoMath.toScreen(currentCustomer.gridX, currentCustomer.gridY, 0f, 0f, 0f)
                            val custCenter = Offset(custBasePos.x, custBasePos.y - 25f)
                            val bubbleCenter = Offset(custBasePos.x, custBasePos.y - 72f)

                            if (IsoMath.distance(unscaledTap, custCenter) < 36f ||
                                IsoMath.distance(unscaledTap, bubbleCenter) < 42f
                            ) {
                                onTapCustomer()
                                return@detectTapGestures
                            }
                        }

                        // 2. Interactive cottage fixtures
                        val cauldronPos = IsoMath.toScreen(1.3f, 4.6f, 0.5f, 0f, 0f)
                        val crystalBallPos = IsoMath.toScreen(3.88f, 1.95f, 1.25f, 0f, 0f)
                        val counterPos = IsoMath.toScreen(4.5f, 1.9f, 0.5f, 0f, 0f)
                        val shelf1Pos = IsoMath.toScreen(0.4f, 4.0f, 1.2f, 0f, 0f)
                        val shelf2Pos = IsoMath.toScreen(2.2f, 0.3f, 2.2f, 0f, 0f)
                        val doorPos = IsoMath.toScreen(4.4f, 0.05f, 1.5f, 0f, 0f)
                        val windowPos = IsoMath.toScreen(0.05f, 2.2f, 2.2f, 0f, 0f)
                        val rugPos = IsoMath.toScreen(3.5f, 3.5f, 0.05f, 0f, 0f)

                        when {
                            IsoMath.distance(unscaledTap, cauldronPos) < 45f -> onTapCauldron()
                            IsoMath.distance(unscaledTap, crystalBallPos) < 32f -> onTapCrystalBall()
                            IsoMath.distance(unscaledTap, counterPos) < 52f -> {
                                onInspectObject(
                                    "Ribbon-Trimmed Counter",
                                    "Honey-oak counter with a lavender lace runner, service bell, and your Crystal Ball."
                                )
                            }
                            IsoMath.distance(unscaledTap, windowPos) < 42f -> {
                                onInspectObject(
                                    "Starlight Cottage Window",
                                    "Moonbeams and flower blooms frame the twilight sky. Tap the Crystal Ball for today's forecast!"
                                )
                            }
                            IsoMath.distance(unscaledTap, shelf1Pos) < 48f -> {
                                onInspectObject(
                                    "Botanical Bookcase",
                                    "Filled with jars of Sunbloom Petals, Sapphire Dew, Wild Mint, and pastel spellbooks."
                                )
                            }
                            IsoMath.distance(unscaledTap, shelf2Pos) < 45f -> {
                                onInspectObject(
                                    "Hanging Herb Rack",
                                    "Fresh cottage herbs drying beside sparkling potion vials. Forage more each morning!"
                                )
                            }
                            IsoMath.distance(unscaledTap, doorPos) < 45f -> {
                                onInspectObject(
                                    "Cottage Entrance",
                                    "Sage-painted oak door adorned with a flower wreath. Adventurers enter when you open shop."
                                )
                            }
                            IsoMath.distance(unscaledTap, rugPos) < 52f -> {
                                onInspectObject(
                                    "Starlight Woven Rug",
                                    "Pastel rose and lavender rug woven with a golden starlight emblem."
                                )
                            }
                            else -> {
                                onInspectObject(
                                    "Enchanted Cottage",
                                    "Tap the Crystal Ball for today's demand, or tap the Cauldron to brew potions!"
                                )
                            }
                        }
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // Starlight twinkling backdrop behind cottage
            PastelCottageArchitecture.drawStarlightBackdrop(this, canvasW, canvasH, twinklePhase)

            val scale = (kotlin.math.min(canvasW / virtualWidth, canvasH / virtualHeight) * 0.92f)
                .coerceIn(0.4f, 2.5f)
            val originX = canvasW / 2f
            val originY = (canvasH / 2f) - (gridSize * IsoMath.HALF_TILE_H * scale * 0.25f)

            withTransform({
                translate(left = originX, top = originY)
                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
            }) {
                PastelCottageArchitecture.drawFloor(this, gridSize, 0f, 0f)
                PastelCottageArchitecture.drawWalls(this, gridSize, wallHeight, 0f, 0f, twinklePhase)
                PastelCottageArchitecture.drawEntranceDoor(this, 0f, 0f, 3.6f, 5.2f, 3.0f)
                PastelCottageFurniture.drawIngredientShelves(this, 0f, 0f)
                PastelCottageFurniture.drawSalesCounter(this, 0f, 0f, 3.5f, 1.6f, 2.0f, 0.85f, 1.15f)
                PastelCottageFurniture.drawStarlightCauldron(
                    drawScope = this,
                    originX = 0f,
                    originY = 0f,
                    gx = 1.3f,
                    gy = 4.6f,
                    brewLiquidColor = Color(cauldronColorHex),
                    twinklePhase = twinklePhase
                )

                if (currentCustomer != null) {
                    IsoCharacterRenderer.drawCustomer(
                        drawScope = this,
                        customer = currentCustomer,
                        originX = 0f,
                        originY = 0f
                    )
                }
            }
        }
    }
}

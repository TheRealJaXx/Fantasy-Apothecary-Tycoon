package com.example.ui.render

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.Customer
import com.example.model.CustomerPhase
import com.example.model.CustomerType
import com.example.model.PotionType

/**
 * Procedural rendering for D&D adventurer customers, order speech bubbles,
 * and live patience indicator bars.
 */
object IsoCharacterRenderer {

    fun drawCustomer(
        drawScope: DrawScope,
        customer: Customer,
        originX: Float,
        originY: Float,
        animBobY: Float = 0f
    ) {
        val basePos = IsoMath.toScreen(customer.gridX, customer.gridY, 0f, originX, originY)
        val charPos = Offset(basePos.x, basePos.y + animBobY)

        // 1. Soft Contact Shadow
        drawScope.drawOval(
            color = Color(0x44000000),
            topLeft = Offset(basePos.x - 14f, basePos.y - 6f),
            size = Size(28f, 12f)
        )

        val cloakColor = Color(customer.type.cloakColorHex)
        val tunicColor = Color(customer.type.tunicColorHex)
        val hatColor = Color(customer.type.hatColorHex)

        // 2. Boots
        val bootColor = Color(0xFF3E2212)
        drawScope.drawRoundRect(
            color = bootColor,
            topLeft = Offset(charPos.x - 7f, charPos.y - 12f),
            size = Size(5f, 12f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawScope.drawRoundRect(
            color = bootColor,
            topLeft = Offset(charPos.x + 2f, charPos.y - 12f),
            size = Size(5f, 12f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // 3. Cloak / Cape
        val cloakPath = Path().apply {
            moveTo(charPos.x - 11f, charPos.y - 32f)
            lineTo(charPos.x + 11f, charPos.y - 32f)
            lineTo(charPos.x + 14f, charPos.y - 10f)
            lineTo(charPos.x - 14f, charPos.y - 10f)
            close()
        }
        drawScope.drawPath(cloakPath, color = cloakColor, style = Fill)
        drawScope.drawPath(cloakPath, color = Color(0x33000000), style = Stroke(width = 1.2f))

        // 4. Torso / Tunic & Belt
        drawScope.drawRoundRect(
            color = tunicColor,
            topLeft = Offset(charPos.x - 9f, charPos.y - 34f),
            size = Size(18f, 22f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawScope.drawRect(
            color = Color(0xFF1E140A),
            topLeft = Offset(charPos.x - 9f, charPos.y - 18f),
            size = Size(18f, 3.5f)
        )
        drawScope.drawRect(
            color = Color(0xFFF7C948),
            topLeft = Offset(charPos.x - 2.5f, charPos.y - 19f),
            size = Size(5f, 5f)
        )

        // 5. Head & Face
        val headCenter = Offset(charPos.x, charPos.y - 41f)
        drawScope.drawCircle(color = Color(0xFFFFDBAC), radius = 7.5f, center = headCenter)
        drawScope.drawCircle(Color(0xFF1F2937), 1.2f, Offset(headCenter.x - 2.8f, headCenter.y - 0.5f))
        drawScope.drawCircle(Color(0xFF1F2937), 1.2f, Offset(headCenter.x + 2.8f, headCenter.y - 0.5f))

        // 6. Distinct D&D Class Headgear
        when (customer.type) {
            CustomerType.APPRENTICE_MAGE -> {
                val hatBrim = Rect(headCenter.x - 12f, headCenter.y - 7f, headCenter.x + 12f, headCenter.y - 3f)
                drawScope.drawOval(hatColor, Offset(hatBrim.left, hatBrim.top), Size(hatBrim.width, hatBrim.height))
                val hatCone = Path().apply {
                    moveTo(headCenter.x - 8f, headCenter.y - 5f)
                    lineTo(headCenter.x + 8f, headCenter.y - 5f)
                    lineTo(headCenter.x - 3f, headCenter.y - 23f)
                    close()
                }
                drawScope.drawPath(hatCone, color = hatColor, style = Fill)
                drawScope.drawRect(
                    color = Color(0xFFF7C948),
                    topLeft = Offset(headCenter.x - 7.5f, headCenter.y - 7f),
                    size = Size(15f, 2.5f)
                )
            }
            CustomerType.ADVENTURER -> {
                val hoodPath = Path().apply {
                    moveTo(headCenter.x - 8.5f, headCenter.y - 2f)
                    lineTo(headCenter.x - 8.5f, headCenter.y - 10f)
                    lineTo(headCenter.x, headCenter.y - 14f)
                    lineTo(headCenter.x + 8.5f, headCenter.y - 10f)
                    lineTo(headCenter.x + 8.5f, headCenter.y - 2f)
                    close()
                }
                drawScope.drawPath(hoodPath, color = hatColor, style = Fill)
                drawScope.drawLine(
                    color = Color(0xFFFDE68A),
                    start = Offset(headCenter.x + 6f, headCenter.y - 8f),
                    end = Offset(headCenter.x + 12f, headCenter.y - 17f),
                    strokeWidth = 2f
                )
            }
            CustomerType.TOWN_GUARD -> {
                drawScope.drawRoundRect(
                    color = hatColor,
                    topLeft = Offset(headCenter.x - 8f, headCenter.y - 12f),
                    size = Size(16f, 10f),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                drawScope.drawRect(
                    color = Color(0xFF1E293B),
                    topLeft = Offset(headCenter.x - 6f, headCenter.y - 4f),
                    size = Size(12f, 1.8f)
                )
                drawScope.drawLine(
                    color = Color(0xFFFB7185),
                    start = Offset(headCenter.x, headCenter.y - 12f),
                    end = Offset(headCenter.x, headCenter.y - 19f),
                    strokeWidth = 3f
                )
            }
        }

        // 7. Leaving states: Potion in hand if served, or sigh puff if missed
        if (customer.phase == CustomerPhase.SERVED_LEAVING) {
            val handPos = Offset(charPos.x + 12f, charPos.y - 22f)
            drawScope.drawCircle(color = customer.requestedPotion.color, radius = 4.5f, center = handPos)
            drawScope.drawCircle(color = Color(0xAAFFFFFF), radius = 2.2f, center = Offset(handPos.x - 1f, handPos.y - 1f))
            drawScope.drawRect(color = Color(0xFFD99B26), topLeft = Offset(handPos.x - 1.5f, handPos.y - 7f), size = Size(3f, 2.5f))
        } else if (customer.phase == CustomerPhase.MISSED_LEAVING) {
            val cloudCenter = Offset(charPos.x, charPos.y - 62f)
            drawScope.drawCircle(color = Color(0xCCFDA4AF), radius = 7f, center = cloudCenter)
            drawScope.drawCircle(color = Color(0xFFE11D48), radius = 2.5f, center = cloudCenter)
        }

        // 8. Order Speech Bubble & Patience Meter when waiting at counter
        if (customer.phase == CustomerPhase.WAITING_FOR_ORDER) {
            drawOrderBubble(
                drawScope = drawScope,
                bubbleCenter = Offset(charPos.x, charPos.y - 72f),
                potion = customer.requestedPotion,
                patience = customer.patienceRemaining
            )
        }
    }

    private fun drawOrderBubble(
        drawScope: DrawScope,
        bubbleCenter: Offset,
        potion: PotionType,
        patience: Float
    ) {
        val bubbleW = 68f
        val bubbleH = 38f
        val bubbleLeft = bubbleCenter.x - (bubbleW / 2f)
        val bubbleTop = bubbleCenter.y - (bubbleH / 2f)

        val tailPath = Path().apply {
            moveTo(bubbleCenter.x - 5f, bubbleTop + bubbleH - 1f)
            lineTo(bubbleCenter.x + 5f, bubbleTop + bubbleH - 1f)
            lineTo(bubbleCenter.x, bubbleTop + bubbleH + 7f)
            close()
        }

        val bubbleBg = Color(0xFFFDF6EE)
        val bubbleBorder = Color(0xFFE8B94B)

        // Shadow
        drawScope.drawRoundRect(
            color = Color(0x33000000),
            topLeft = Offset(bubbleLeft + 2f, bubbleTop + 2f),
            size = Size(bubbleW, bubbleH),
            cornerRadius = CornerRadius(10f, 10f)
        )

        // Bubble Body
        drawScope.drawRoundRect(
            color = bubbleBg,
            topLeft = Offset(bubbleLeft, bubbleTop),
            size = Size(bubbleW, bubbleH),
            cornerRadius = CornerRadius(10f, 10f)
        )
        drawScope.drawPath(tailPath, color = bubbleBg, style = Fill)

        drawScope.drawRoundRect(
            color = bubbleBorder,
            topLeft = Offset(bubbleLeft, bubbleTop),
            size = Size(bubbleW, bubbleH),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(width = 1.6f)
        )
        drawScope.drawPath(tailPath, color = bubbleBorder, style = Stroke(width = 1.6f))

        // Potion Icon inside bubble
        val potionPos = Offset(bubbleLeft + 18f, bubbleCenter.y - 3f)
        drawScope.drawCircle(color = potion.color, radius = 6.5f, center = potionPos)
        drawScope.drawCircle(color = Color(0xAAFFFFFF), radius = 2.4f, center = Offset(potionPos.x - 2f, potionPos.y - 2f))
        drawScope.drawRect(color = Color(0xFFD99B26), topLeft = Offset(potionPos.x - 2f, potionPos.y - 10f), size = Size(4f, 3.2f))

        // Coin icon
        val coinPos = Offset(bubbleLeft + 44f, bubbleCenter.y - 3f)
        drawScope.drawCircle(color = Color(0xFFF7C948), radius = 5.5f, center = coinPos)
        drawScope.drawCircle(color = Color(0xFFFEF08A), radius = 3.2f, center = coinPos)

        // Patience Meter along bottom of bubble
        val barWidth = bubbleW - 14f
        val barX = bubbleLeft + 7f
        val barY = bubbleTop + bubbleH - 9f
        drawScope.drawRoundRect(
            color = Color(0xFFE5E7EB),
            topLeft = Offset(barX, barY),
            size = Size(barWidth, 4.5f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        val patienceColor = when {
            patience > 0.55f -> Color(0xFF4ADE80)
            patience > 0.25f -> Color(0xFFFBBF24)
            else -> Color(0xFFFB7185)
        }
        drawScope.drawRoundRect(
            color = patienceColor,
            topLeft = Offset(barX, barY),
            size = Size((barWidth * patience).coerceAtLeast(2f), 4.5f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}

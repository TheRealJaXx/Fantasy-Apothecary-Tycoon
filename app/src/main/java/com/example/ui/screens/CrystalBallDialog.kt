package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DailyForecast
import com.example.model.DayPhase
import com.example.model.PotionType
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.LightAmber
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightIndigo
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight

@Composable
fun CrystalBallDialog(
    forecast: DailyForecast,
    dayPhase: DayPhase,
    potionStock: Map<PotionType, Int>,
    onOpenMarket: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = StarlightTwilight,
            tonalElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelLavender),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("crystal_ball_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(StarlightPlum, CircleShape)
                                .border(1.5.dp, PastelLavender, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = LightAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Crystal Ball Forecast",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ButtercreamGold
                            )
                            Text(
                                text = "Day ${forecast.day} Starlight Divination",
                                fontSize = 11.sp,
                                color = PastelLavender
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_crystal_ball_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Crystal Ball",
                            tint = SoftCream
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0x33FFFFFF)
                )

                // Procedural Glowing Crystal Ball Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(106.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF4C3C82), StarlightIndigo)
                            )
                        )
                        .border(1.dp, PastelLavender.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(120.dp, 96.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height * 0.46f
                        // Aura
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0x66C4B5FD), Color(0x00C4B5FD)),
                                center = Offset(cx, cy),
                                radius = 44f
                            ),
                            radius = 44f,
                            center = Offset(cx, cy)
                        )
                        // Gold stand
                        drawRoundRect(
                            color = ButtercreamGold,
                            topLeft = androidx.compose.ui.geometry.Offset(cx - 22f, cy + 24f),
                            size = androidx.compose.ui.geometry.Size(44f, 10f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                        // Crystal sphere
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFE9D5FF), Color(0xFF7C3AED)),
                                center = Offset(cx - 10f, cy - 10f),
                                radius = 32f
                            ),
                            radius = 28f,
                            center = Offset(cx, cy)
                        )
                        // Sparkles inside orb
                        drawCircle(color = LightAmber, radius = 2.5f, center = Offset(cx - 7f, cy + 4f))
                        drawCircle(color = Color.White, radius = 2f, center = Offset(cx + 9f, cy - 5f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Event Headline & Expected Adventurers
                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = forecast.eventTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ButtercreamGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = forecast.headline,
                            fontSize = 12.sp,
                            color = SoftCream,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = PastelLavender,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Expected Adventurers Today: ${forecast.expectedCustomers}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = LightAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Potion Demand Breakdown
                Text(
                    text = "Predicted Potion Demand vs. Your Shelf Stock",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PastelLavender
                )
                Spacer(modifier = Modifier.height(6.dp))

                PotionType.values().forEach { potion ->
                    val pct = forecast.demandFor(potion)
                    val onShelf = potionStock[potion] ?: 0
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${potion.displayName} ($pct%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(potion.colorHex)
                            )
                            Text(
                                text = "On Shelf: $onShelf",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (onShelf == 0) Color(0xFFFB7185) else SoftCream
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { pct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(potion.colorHex),
                            trackColor = Color(0x33FFFFFF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                if (dayPhase == DayPhase.MORNING_PREP) {
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenMarket()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("forecast_to_market_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtercreamGold,
                            contentColor = Color(0xFF231604)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Visit Morning Market & Garden", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("crystal_ball_return_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Cottage", color = SoftCream, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DailyForecast
import com.example.model.IngredientType
import com.example.model.PotionType
import com.example.model.PriceTrend
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.LightAmber
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelRose
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightMist
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight

@Composable
fun MorningMarketDialog(
    gold: Int,
    forecast: DailyForecast,
    ingredientPantry: Map<IngredientType, Int>,
    foragedToday: Boolean,
    isMorningPrep: Boolean,
    onForageGarden: () -> Unit,
    onBuyIngredient: (IngredientType, Int) -> Unit,
    onBuyTrioBundle: () -> Unit,
    onDismiss: () -> Unit
) {
    val bundleCost = IngredientType.values().sumOf { forecast.ingredientPrices[it] ?: it.basePrice }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = StarlightTwilight,
            tonalElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ButtercreamGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("morning_market_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                                .size(34.dp)
                                .background(StarlightPlum, CircleShape)
                                .border(1.5.dp, ButtercreamGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBasket,
                                contentDescription = null,
                                tint = ButtercreamGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Market & Garden",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ButtercreamGold
                            )
                            Text(
                                text = "Vault: $gold G • Day ${forecast.day}",
                                fontSize = 11.sp,
                                color = LightAmber,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_market_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Market",
                            tint = SoftCream
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0x33FFFFFF)
                )

                // 1. Cottage Garden Forage Card (Free once each morning!)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1D3B32)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PastelMint.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = PastelMint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cottage Garden Forage",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SoftCream
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Collect +1 of each wild herb free once per morning!",
                                fontSize = 11.sp,
                                color = Color(0xFFA7F3D0)
                            )
                        }

                        Button(
                            onClick = onForageGarden,
                            enabled = isMorningPrep && !foragedToday,
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("forage_garden_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelMint,
                                contentColor = Color(0xFF052E16),
                                disabledContainerColor = Color(0xFF334155),
                                disabledContentColor = Color(0xFF94A3B8)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (foragedToday) "Collected" else "Forage Free",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!isMorningPrep) {
                    Text(
                        text = "The morning market stalls pack up once your shop is open! Use Pixie Express on urgent orders if needed.",
                        fontSize = 12.sp,
                        color = PastelRose,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 2. Fluctuating Ingredient Market Cards
                IngredientType.values().forEach { ing ->
                    val price = forecast.ingredientPrices[ing] ?: ing.basePrice
                    val trend = forecast.ingredientTrends[ing] ?: PriceTrend.NORMAL
                    val inPantry = ingredientPantry[ing] ?: 0
                    val matchingPotion = PotionType.values().first { it.requiredIngredient == ing }

                    MarketIngredientRow(
                        ingredient = ing,
                        potionName = matchingPotion.displayName,
                        unitPrice = price,
                        trend = trend,
                        inPantry = inPantry,
                        canBuyOne = isMorningPrep && gold >= price,
                        canBuyThree = isMorningPrep && gold >= (price * 3),
                        onBuyOne = { onBuyIngredient(ing, 1) },
                        onBuyThree = { onBuyIngredient(ing, 3) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 3. Trio Bundle Button
                Button(
                    onClick = onBuyTrioBundle,
                    enabled = isMorningPrep && gold >= bundleCost,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("buy_trio_bundle_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtercreamGold,
                        contentColor = Color(0xFF231604),
                        disabledContainerColor = Color(0xFF3F3859),
                        disabledContentColor = Color(0xFF9CA3AF)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Buy Trio Bundle (1 of Each) • $bundleCost G",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("market_done_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Cottage", color = SoftCream, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MarketIngredientRow(
    ingredient: IngredientType,
    potionName: String,
    unitPrice: Int,
    trend: PriceTrend,
    inPantry: Int,
    canBuyOne: Boolean,
    canBuyThree: Boolean,
    onBuyOne: () -> Unit,
    onBuyThree: () -> Unit
) {
    val trendLabel = when (trend) {
        PriceTrend.BARGAIN -> "BARGAIN"
        PriceTrend.NORMAL -> "NORMAL"
        PriceTrend.HIGH_DEMAND -> "HIGH DEMAND"
    }
    val trendColor = when (trend) {
        PriceTrend.BARGAIN -> PastelMint
        PriceTrend.NORMAL -> PastelLavender
        PriceTrend.HIGH_DEMAND -> PastelRose
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = StarlightPlum),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x28FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(ingredient.color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = ingredient.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SoftCream
                        )
                        Text(
                            text = "Brews $potionName • In Pantry: $inPantry",
                            fontSize = 11.sp,
                            color = Color(0xFFD0C4EC)
                        )
                    }
                }

                Surface(
                    color = trendColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, trendColor)
                ) {
                    Text(
                        text = trendLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = trendColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBuyOne,
                    enabled = canBuyOne,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("buy_1_${ingredient.name}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StarlightMist,
                        contentColor = SoftCream
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+1 ($unitPrice G)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBuyThree,
                    enabled = canBuyThree,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("buy_3_${ingredient.name}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StarlightMist,
                        contentColor = ButtercreamGold
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+3 (${unitPrice * 3} G)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

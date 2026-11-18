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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.DayPhase
import com.example.model.IngredientType
import com.example.model.PotionType
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.LightAmber
import com.example.ui.theme.OnSurfaceMuted
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight
import com.example.viewmodel.GameViewModel

@Composable
fun ApothecaryMenuDialog(
    day: Int,
    gold: Int,
    inventoryCount: Int,
    potionStock: Map<PotionType, Int>,
    ingredientPantry: Map<IngredientType, Int>,
    dayPhase: DayPhase,
    totalCustomersServed: Int,
    onDismiss: () -> Unit
) {
    val phaseLabel = when (dayPhase) {
        DayPhase.MORNING_PREP -> "Morning Prep"
        DayPhase.SHOP_OPEN -> "Open for Business"
        DayPhase.DAY_SUMMARY -> "Evening Ledger"
    }
    val phaseColor = when (dayPhase) {
        DayPhase.MORNING_PREP -> ButtercreamGold
        DayPhase.SHOP_OPEN -> PastelMint
        DayPhase.DAY_SUMMARY -> PastelLavender
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = StarlightTwilight,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ButtercreamGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("menu_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
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
                                .border(1.5.dp, ButtercreamGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Apothecary Menu",
                                tint = ButtercreamGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Cottage Ledger",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ButtercreamGold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Menu",
                            tint = SoftCream
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0x33FFFFFF)
                )

                // Stats overview card
                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Shop Overview",
                            fontWeight = FontWeight.Bold,
                            color = SoftCream,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LedgerStatRow("Current Date", "Day $day", SoftCream)
                        LedgerStatRow("Vault Gold", "$gold G", ButtercreamGold)
                        LedgerStatRow(
                            "Shelved Potions",
                            "$inventoryCount / ${GameViewModel.MAX_SHELF_CAPACITY} bottles",
                            PastelMint
                        )
                        LedgerStatRow("Patrons Served", "$totalCustomersServed adventurers", LightAmber)
                        LedgerStatRow("Day Phase", phaseLabel, phaseColor)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Shelves & Pantry Breakdown
                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Shelf Bottles & Herb Pantry",
                            fontWeight = FontWeight.Bold,
                            color = SoftCream,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PotionType.values().forEach { potion ->
                            LedgerStatRow(
                                label = potion.displayName,
                                value = "${potionStock[potion] ?: 0} bottled",
                                valueColor = potion.color
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = Color(0x22FFFFFF)
                        )
                        IngredientType.values().forEach { ing ->
                            LedgerStatRow(
                                label = ing.displayName,
                                value = "${ingredientPantry[ing] ?: 0} in pantry",
                                valueColor = ing.color
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guide card
                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PastelLavender,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cottage Tycoon Guide",
                                fontWeight = FontWeight.Bold,
                                color = SoftCream,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Morning Prep: Peek at your Crystal Ball to forecast D&D adventurer demand.\n" +
                                "• Market & Garden: Forage free cottage herbs once each morning and buy bargain ingredients.\n" +
                                "• Starlight Cauldron: Brew Health, Mana, and Stamina potions before opening.\n" +
                                "• Open Shop: Serve travelers at the counter before patience runs out—or call Pixie Express in a pinch!",
                            color = OnSurfaceMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("resume_game_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtercreamGold,
                        contentColor = Color(0xFF231604)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Return to Cottage", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun LedgerStatRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = OnSurfaceMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

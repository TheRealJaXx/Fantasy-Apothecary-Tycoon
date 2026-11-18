package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BrewingStep
import com.example.model.IngredientType
import com.example.model.PotionType
import com.example.model.RecipeBook
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.HoneyWood
import com.example.ui.theme.LightAmber
import com.example.ui.theme.OnSurfaceMuted
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelRose
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightIndigo
import com.example.ui.theme.StarlightMist
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight
import com.example.viewmodel.GameViewModel

@Composable
fun BrewingDialog(
    selectedPotion: PotionType,
    brewingStep: BrewingStep,
    stirCount: Int,
    inventoryCount: Int,
    potionStock: Map<PotionType, Int>,
    ingredientPantry: Map<IngredientType, Int>,
    onSelectPotion: (PotionType) -> Unit,
    onAddIngredients: () -> Unit,
    onStir: () -> Unit,
    onBottle: () -> Unit,
    onQuickBrew: (PotionType) -> Unit,
    onOpenMarket: () -> Unit,
    onDismiss: () -> Unit
) {
    val recipe = RecipeBook.forType(selectedPotion)
    val liquidColor = selectedPotion.color
    val primaryIng = recipe.primaryIngredient
    val pantryCount = ingredientPantry[primaryIng] ?: 0
    val shelfCountForSelected = potionStock[selectedPotion] ?: 0
    val shelvesFull = inventoryCount >= GameViewModel.MAX_SHELF_CAPACITY

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = StarlightTwilight,
            tonalElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ButtercreamGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("brewing_dialog")
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
                                .size(36.dp)
                                .background(StarlightPlum, CircleShape)
                                .border(1.5.dp, ButtercreamGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = LightAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Starlight Cauldron",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ButtercreamGold
                            )
                            Text(
                                text = "Shelves: $inventoryCount/${GameViewModel.MAX_SHELF_CAPACITY} bottles",
                                fontSize = 11.sp,
                                color = PastelLavender
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_brewing_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Brewing",
                            tint = SoftCream
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0x33FFFFFF)
                )

                // Recipe Selection Row (Health, Mana, Stamina)
                Text(
                    text = "Choose a Formula:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurfaceMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PotionType.values().forEach { potionType ->
                        val isSelected = potionType == selectedPotion
                        val badgeBorder = if (isSelected) Color(potionType.colorHex) else Color(0x33FFFFFF)
                        val badgeBg = if (isSelected) Color(potionType.colorHex).copy(alpha = 0.24f) else StarlightPlum
                        val shelfQty = potionStock[potionType] ?: 0

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = badgeBg,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectPotion(potionType) }
                                .testTag("recipe_tab_${potionType.name}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(potionType.colorHex), CircleShape)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = potionType.shortName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SoftCream else OnSurfaceMuted
                                )
                                Text(
                                    text = "+${potionType.price} G",
                                    fontSize = 10.sp,
                                    color = ButtercreamGold
                                )
                                Text(
                                    text = "Shelf: $shelfQty",
                                    fontSize = 10.sp,
                                    color = if (shelfQty == 0) PastelRose else PastelMint
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Procedural Enchanted Brass-and-Starlight Cauldron Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(124.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(StarlightMist, StarlightIndigo)
                            )
                        )
                        .border(1.dp, PastelLavender.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(180.dp, 116.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height * 0.62f

                        // Cozy warm pastel hearth glow
                        val hearthGlow = Brush.radialGradient(
                            colors = listOf(Color(0xFFFDE68A), Color(0xFFFB7185), Color(0x00FB7185)),
                            center = Offset(cx, cy + 18f),
                            radius = 44f
                        )
                        drawCircle(brush = hearthGlow, radius = 42f, center = Offset(cx, cy + 18f))

                        // Honey-wood logs
                        drawLine(
                            color = Color(0xFF8C5D38),
                            start = Offset(cx - 25f, cy + 24f),
                            end = Offset(cx + 25f, cy + 16f),
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color(0xFFA67346),
                            start = Offset(cx - 22f, cy + 16f),
                            end = Offset(cx + 23f, cy + 24f),
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )

                        // Brass feet
                        val brassColor = Color(0xFFD99B26)
                        drawLine(brassColor, Offset(cx - 28f, cy), Offset(cx - 36f, cy + 26f), 4.5f)
                        drawLine(brassColor, Offset(cx + 28f, cy), Offset(cx + 36f, cy + 26f), 4.5f)
                        drawLine(brassColor, Offset(cx, cy + 10f), Offset(cx, cy + 28f), 4.5f)

                        // Enchanted plum-brass cauldron belly
                        val bodyRadius = 37f
                        val bodyBrush = Brush.radialGradient(
                            colors = listOf(Color(0xFF6B53A8), Color(0xFF3E2D6B), Color(0xFF231845)),
                            center = Offset(cx - 10f, cy - 8f),
                            radius = bodyRadius * 1.3f
                        )
                        drawCircle(brush = bodyBrush, radius = bodyRadius, center = Offset(cx, cy))

                        // Gold cauldron rim
                        val rimW = 70f
                        val rimH = 24f
                        val rimRect = Rect(cx - rimW / 2f, cy - 30f, cx + rimW / 2f, cy - 30f + rimH)
                        drawOval(
                            color = Color(0xFF54418C),
                            topLeft = Offset(rimRect.left, rimRect.top),
                            size = Size(rimRect.width, rimRect.height)
                        )
                        drawOval(
                            color = ButtercreamGold,
                            topLeft = Offset(rimRect.left, rimRect.top),
                            size = Size(rimRect.width, rimRect.height),
                            style = Stroke(width = 3f)
                        )

                        // Liquid potion surface inside cauldron
                        val isSimmering = brewingStep != BrewingStep.SELECT_RECIPE
                        val displayLiquid = if (isSimmering) liquidColor else Color(0xFF5D4C8A)
                        val innerLiquid = Rect(
                            rimRect.left + 4f,
                            rimRect.top + 4f,
                            rimRect.right - 4f,
                            rimRect.bottom - 4f
                        )

                        val liquidBrush = Brush.radialGradient(
                            colors = listOf(
                                Color(
                                    red = (displayLiquid.red * 1.35f).coerceIn(0f, 1f),
                                    green = (displayLiquid.green * 1.35f).coerceIn(0f, 1f),
                                    blue = (displayLiquid.blue * 1.35f).coerceIn(0f, 1f),
                                    alpha = 1f
                                ),
                                displayLiquid,
                                Color(
                                    red = (displayLiquid.red * 0.55f).coerceIn(0f, 1f),
                                    green = (displayLiquid.green * 0.55f).coerceIn(0f, 1f),
                                    blue = (displayLiquid.blue * 0.55f).coerceIn(0f, 1f),
                                    alpha = 1f
                                )
                            ),
                            center = Offset(cx, rimRect.top + rimH / 2f),
                            radius = rimW / 2f
                        )
                        drawOval(
                            brush = liquidBrush,
                            topLeft = Offset(innerLiquid.left, innerLiquid.top),
                            size = Size(innerLiquid.width, innerLiquid.height)
                        )

                        // Starlight bubbles & shimmer puffs when simmering
                        if (isSimmering) {
                            val bubbles = listOf(
                                Offset(cx - 14f, rimRect.top + 8f) to 4f,
                                Offset(cx + 12f, rimRect.top + 14f) to 3.5f,
                                Offset(cx - 2f, rimRect.top + 10f) to 2.8f,
                                Offset(cx + 20f, rimRect.top + 9f) to 2.5f
                            )
                            for ((bPos, bRadius) in bubbles) {
                                drawCircle(color = Color(0xCCFFFFFF), radius = bRadius, center = bPos)
                                drawCircle(
                                    color = displayLiquid,
                                    radius = bRadius,
                                    center = bPos,
                                    style = Stroke(width = 1f)
                                )
                            }

                            drawCircle(
                                color = LightAmber.copy(alpha = 0.35f),
                                radius = 7f,
                                center = Offset(cx - 10f, cy - 42f)
                            )
                            drawCircle(
                                color = displayLiquid.copy(alpha = 0.28f),
                                radius = 10f,
                                center = Offset(cx + 9f, cy - 52f)
                            )
                        }

                        // Honey-oak stirring spoon
                        val stirAngleOffset = (stirCount % 2) * 8f
                        drawLine(
                            color = HoneyWood,
                            start = Offset(cx - 28f + stirAngleOffset, cy + 2f),
                            end = Offset(cx + 24f + stirAngleOffset, cy - 48f),
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = ButtercreamGold,
                            start = Offset(cx + 14f + stirAngleOffset, cy - 36f),
                            end = Offset(cx + 25f + stirAngleOffset, cy - 50f),
                            strokeWidth = 7f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Recipe Description, Pantry Stock & Notes Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = recipe.potionType.displayName,
                                fontWeight = FontWeight.Bold,
                                color = recipe.potionType.color,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "On Shelf: $shelfCountForSelected",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PastelLavender
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = recipe.description,
                            fontSize = 12.sp,
                            color = SoftCream,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pantry pill for required raw ingredient
                        Surface(
                            color = if (pantryCount > 0) PastelMint.copy(alpha = 0.16f) else PastelRose.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (pantryCount > 0) PastelMint else PastelRose
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Requires: 1x ${primaryIng.displayName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SoftCream
                                )
                                Text(
                                    text = "In Pantry: $pantryCount",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pantryCount > 0) PastelMint else PastelRose
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        recipe.secondaryNotes.forEach { note ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 1.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = recipe.potionType.color,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = note,
                                    fontSize = 11.sp,
                                    color = OnSurfaceMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Brewing Action Steps
                when (brewingStep) {
                    BrewingStep.SELECT_RECIPE -> {
                        if (pantryCount <= 0) {
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onOpenMarket()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("brewing_to_market_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBasket,
                                    contentDescription = null,
                                    tint = ButtercreamGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Out of ${primaryIng.displayName} • Visit Market",
                                    color = ButtercreamGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = onAddIngredients,
                                enabled = !shelvesFull,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("add_ingredients_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ButtercreamGold,
                                    contentColor = Color(0xFF231604),
                                    disabledContainerColor = Color(0xFF4B5563),
                                    disabledContentColor = Color(0xFF9CA3AF)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (shelvesFull) {
                                        "Shelves Full (${GameViewModel.MAX_SHELF_CAPACITY}/${GameViewModel.MAX_SHELF_CAPACITY})"
                                    } else {
                                        "Add 1x ${primaryIng.displayName} & Brew"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { onQuickBrew(selectedPotion) },
                                enabled = !shelvesFull,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("quick_brew_dialog_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = PastelMint,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quick Brew +1 ${selectedPotion.shortName} (Uses 1 Herb)",
                                    color = PastelMint,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    BrewingStep.ADD_INGREDIENTS,
                    BrewingStep.STIRRING -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Infusing starlight...",
                                    fontSize = 12.sp,
                                    color = OnSurfaceMuted
                                )
                                Text(
                                    text = "$stirCount / 3 Stirs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ButtercreamGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (stirCount.toFloat() / 3f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = recipe.potionType.color,
                                trackColor = Color(0x33FFFFFF)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onStir,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("stir_cauldron_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StarlightMist,
                                    contentColor = LightAmber
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Stir with Spoon ($stirCount/3)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    BrewingStep.READY_TO_BOTTLE -> {
                        Button(
                            onClick = onBottle,
                            enabled = !shelvesFull,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("bottle_potion_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelMint,
                                contentColor = Color(0xFF052E16),
                                disabledContainerColor = Color(0xFF4B5563),
                                disabledContentColor = Color(0xFF9CA3AF)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (!shelvesFull) {
                                    "Bottle ${selectedPotion.shortName} (+1 to Shelf)"
                                } else {
                                    "Shelves Full (${GameViewModel.MAX_SHELF_CAPACITY}/${GameViewModel.MAX_SHELF_CAPACITY})"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

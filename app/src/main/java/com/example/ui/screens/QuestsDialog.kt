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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Quest
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.LightAmber
import com.example.ui.theme.OnSurfaceMuted
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightMist
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight

@Composable
fun QuestsDialog(
    quests: List<Quest>,
    onClaimReward: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val completedCount = quests.count { it.isCompleted }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = StarlightTwilight,
            tonalElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ButtercreamGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("quests_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = LightAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Cottage Quests",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ButtercreamGold
                            )
                            Text(
                                text = "Guild Tasks • $completedCount/${quests.size} Complete",
                                fontSize = 11.sp,
                                color = PastelLavender
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_quests_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Quests",
                            tint = SoftCream
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0x33FFFFFF)
                )

                // Info banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = PastelLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Complete daily gathering, brewing, and counter goals for bonus Gold and free cottage ingredients!",
                            fontSize = 11.sp,
                            color = OnSurfaceMuted,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quests List
                quests.forEach { quest ->
                    QuestCardItem(
                        quest = quest,
                        onClaim = { onClaimReward(quest.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("resume_from_quests_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtercreamGold,
                        contentColor = Color(0xFF231604)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Return to Cottage", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun QuestCardItem(
    quest: Quest,
    onClaim: () -> Unit
) {
    val cardBorder = when {
        quest.isClaimed -> Color(0x33FFFFFF)
        quest.isCompleted -> ButtercreamGold
        else -> Color(0x33FFFFFF)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (quest.isCompleted && !quest.isClaimed) StarlightMist else StarlightPlum
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quest_card_${quest.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Quest Title & Reward Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quest.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (quest.isCompleted && !quest.isClaimed) LightAmber else SoftCream,
                    modifier = Modifier.weight(1f).padding(end = 6.dp)
                )

                // Reward badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = ButtercreamGold.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ButtercreamGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = ButtercreamGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "+${quest.rewardGold} G",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LightAmber
                            )
                        }
                    }

                    val bonusIng = quest.rewardBonusIngredient
                    if (bonusIng != null && quest.rewardBonusQty > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = bonusIng.color.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, bonusIng.color)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = bonusIng.color,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "+${quest.rewardBonusQty} ${bonusIng.displayName.substringBefore(" ")}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftCream
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quest.description,
                fontSize = 12.sp,
                color = OnSurfaceMuted,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progress",
                            fontSize = 10.sp,
                            color = PastelLavender
                        )
                        Text(
                            text = "${quest.currentProgress} / ${quest.targetGoal}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quest.isCompleted) PastelMint else SoftCream
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { quest.progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isCompleted) PastelMint else ButtercreamGold,
                        trackColor = Color(0x33FFFFFF)
                    )
                }

                when {
                    quest.isClaimed -> {
                        Surface(
                            color = PastelMint.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PastelMint)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PastelMint,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Claimed",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PastelMint
                                )
                            }
                        }
                    }

                    quest.isCompleted -> {
                        Button(
                            onClick = onClaim,
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("claim_quest_button_${quest.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ButtercreamGold,
                                contentColor = Color(0xFF231604)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Claim",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = "In Progress",
                            fontSize = 11.sp,
                            color = PastelLavender
                        )
                    }
                }
            }
        }
    }
}

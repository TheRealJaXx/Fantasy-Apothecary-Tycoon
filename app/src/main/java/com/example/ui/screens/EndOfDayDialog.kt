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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.model.DailySummary
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.LightAmber
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelRose
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight

@Composable
fun EndOfDayDialog(
    summary: DailySummary,
    totalGold: Int,
    onStartNextDay: () -> Unit
) {
    val profitColor = if (summary.netProfit >= 0) PastelMint else PastelRose
    val profitPrefix = if (summary.netProfit >= 0) "+" else ""

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = StarlightTwilight,
            tonalElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ButtercreamGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("end_of_day_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(StarlightPlum, CircleShape)
                            .border(1.5.dp, PastelLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = LightAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Day ${summary.dayNumber} Evening Ledger",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ButtercreamGold
                        )
                        Text(
                            text = "Starlight falls over your cottage",
                            fontSize = 11.sp,
                            color = PastelLavender
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0x33FFFFFF)
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = StarlightPlum),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        SummaryRow("Adventurers Served", "${summary.customersServed}", PastelMint)
                        Spacer(modifier = Modifier.height(6.dp))
                        SummaryRow(
                            "Missed Sales (Out of Stock)",
                            "${summary.customersMissed}",
                            if (summary.customersMissed == 0) PastelMint else PastelRose
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0x22FFFFFF)
                        )
                        SummaryRow("Potion Sales Revenue", "+${summary.goldEarned} G", LightAmber)
                        Spacer(modifier = Modifier.height(6.dp))
                        SummaryRow("Supplies & Express Spent", "-${summary.goldSpent} G", PastelRose)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0x33FFFFFF)
                        )
                        SummaryRow(
                            "Net Profit Today",
                            "$profitPrefix${summary.netProfit} G",
                            profitColor,
                            bold = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SummaryRow("Total Cottage Vault", "$totalGold G", ButtercreamGold, bold = true)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val tipText = when {
                    summary.customersMissed > 0 ->
                        "Tip: A few adventurers left empty-handed! Check tomorrow's Crystal Ball and stock extra ingredients in the morning."
                    summary.customersServed > 0 ->
                        "Splendid work, Cottage Keeper! Every traveler left happy with your magical brews."
                    else ->
                        "Rest up! Tomorrow morning brings a fresh Crystal Ball forecast and garden herbs to forage."
                }
                Text(
                    text = tipText,
                    fontSize = 12.sp,
                    color = Color(0xFFD0C4EC),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onStartNextDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("start_next_day_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtercreamGold,
                        contentColor = Color(0xFF231604)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sleep & Begin Day ${summary.dayNumber + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (bold) 14.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) SoftCream else Color(0xFFD0C4EC)
        )
        Text(
            text = value,
            fontSize = if (bold) 14.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

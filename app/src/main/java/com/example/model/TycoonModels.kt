package com.example.model

import androidx.compose.ui.graphics.Color

enum class DayPhase {
    MORNING_PREP, // Unlimited time to check Crystal Ball, buy/forage ingredients, & pre-brew
    SHOP_OPEN,    // Day shift wave: D&D customers arrive with patience timers
    DAY_SUMMARY   // End-of-day profit & satisfaction ledger before starting next morning
}

enum class IngredientType(
    val displayName: String,
    val basePrice: Int,
    val colorHex: Long,
    val lore: String
) {
    SUNBLOOM_PETAL(
        displayName = "Sunbloom Petal",
        basePrice = 10,
        colorHex = 0xFFFB7185,
        lore = "Warm rose-gold petals used to brew Ruby Health Elixirs."
    ),
    SAPPHIRE_DEW(
        displayName = "Sapphire Dew",
        basePrice = 12,
        colorHex = 0xFF60A5FA,
        lore = "Starlight-distilled crystal drops used to brew Mana Draughts."
    ),
    WILD_MINT(
        displayName = "Wild Mint Leaf",
        basePrice = 8,
        colorHex = 0xFF4ADE80,
        lore = "Crisp garden sprigs used to brew Emerald Stamina Tonics."
    );

    val color: Color get() = Color(colorHex)
}

enum class PriceTrend {
    BARGAIN,
    NORMAL,
    HIGH_DEMAND
}

data class DailyForecast(
    val day: Int,
    val eventTitle: String,
    val headline: String,
    val expectedCustomers: Int,
    val healthDemandPct: Int,
    val manaDemandPct: Int,
    val staminaDemandPct: Int,
    val ingredientPrices: Map<IngredientType, Int>,
    val ingredientTrends: Map<IngredientType, PriceTrend>
) {
    fun demandFor(potion: PotionType): Int = when (potion) {
        PotionType.HEALTH_POTION -> healthDemandPct
        PotionType.MANA_POTION -> manaDemandPct
        PotionType.STAMINA_POTION -> staminaDemandPct
    }
}

data class DailySummary(
    val dayNumber: Int,
    val customersServed: Int,
    val customersMissed: Int,
    val goldEarned: Int,
    val goldSpent: Int,
    val foragedToday: Boolean
) {
    val netProfit: Int get() = goldEarned - goldSpent
}

object ForecastGenerator {

    private data class ForecastTemplate(
        val title: String,
        val headline: String,
        val healthPct: Int,
        val manaPct: Int,
        val staminaPct: Int
    )

    private val templates = listOf(
        ForecastTemplate(
            title = "Goblin Skirmish Alert",
            headline = "Rangers and guards are patrolling the borders! Expect heavy demand for Health Elixirs.",
            healthPct = 55,
            manaPct = 20,
            staminaPct = 25
        ),
        ForecastTemplate(
            title = "Arcane Academy Exams",
            headline = "Young mages are cramming spells by starlight—stock up on Sapphire Dew for Mana Draughts!",
            healthPct = 20,
            manaPct = 55,
            staminaPct = 25
        ),
        ForecastTemplate(
            title = "Grand Guild March",
            headline = "Royal Guards have double-shifts today. Stamina Tonics will fly off the shelves!",
            healthPct = 25,
            manaPct = 20,
            staminaPct = 55
        ),
        ForecastTemplate(
            title = "Starlight Market Fair",
            headline = "A balanced crowd of adventurers is passing through town—keep a steady mix of all three brews.",
            healthPct = 35,
            manaPct = 35,
            staminaPct = 30
        ),
        ForecastTemplate(
            title = "Dungeon Raid Departure",
            headline = "Party leaders are gearing up at dawn! High call for both Health Elixirs and Mana Draughts.",
            healthPct = 45,
            manaPct = 40,
            staminaPct = 15
        )
    )

    fun generateForDay(day: Int): DailyForecast {
        val template = templates[(day - 1) % templates.size]
        // Scale customer volume gently across days (4 on Day 1 -> up to 8 later)
        val expectedCount = (4 + ((day - 1) / 2)).coerceAtMost(8)

        val prices = mutableMapOf<IngredientType, Int>()
        val trends = mutableMapOf<IngredientType, PriceTrend>()

        for (ing in IngredientType.values()) {
            val pct = when (ing) {
                IngredientType.SUNBLOOM_PETAL -> template.healthPct
                IngredientType.SAPPHIRE_DEW -> template.manaPct
                IngredientType.WILD_MINT -> template.staminaPct
            }
            when {
                pct >= 50 -> {
                    prices[ing] = (ing.basePrice * 1.3f).toInt()
                    trends[ing] = PriceTrend.HIGH_DEMAND
                }
                pct <= 22 -> {
                    prices[ing] = (ing.basePrice * 0.7f).toInt().coerceAtLeast(5)
                    trends[ing] = PriceTrend.BARGAIN
                }
                else -> {
                    prices[ing] = ing.basePrice
                    trends[ing] = PriceTrend.NORMAL
                }
            }
        }

        return DailyForecast(
            day = day,
            eventTitle = template.title,
            headline = template.headline,
            expectedCustomers = expectedCount,
            healthDemandPct = template.healthPct,
            manaDemandPct = template.manaPct,
            staminaDemandPct = template.staminaPct,
            ingredientPrices = prices,
            ingredientTrends = trends
        )
    }
}

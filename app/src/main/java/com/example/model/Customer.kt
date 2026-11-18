package com.example.model

import androidx.compose.ui.graphics.Color

enum class PotionType(
    val displayName: String,
    val shortName: String,
    val price: Int,
    val colorHex: Long,
    val requiredIngredient: IngredientType,
    val description: String
) {
    HEALTH_POTION(
        displayName = "Health Elixir",
        shortName = "Health",
        price = 26,
        colorHex = 0xFFF43F5E,
        requiredIngredient = IngredientType.SUNBLOOM_PETAL,
        description = "Restores vitality and mends battle scrapes with warm sunbloom petals."
    ),
    MANA_POTION(
        displayName = "Mana Draught",
        shortName = "Mana",
        price = 32,
        colorHex = 0xFF38BDF8,
        requiredIngredient = IngredientType.SAPPHIRE_DEW,
        description = "Recharges arcane focus using starlight-distilled sapphire dew."
    ),
    STAMINA_POTION(
        displayName = "Stamina Tonic",
        shortName = "Stamina",
        price = 22,
        colorHex = 0xFF34D399,
        requiredIngredient = IngredientType.WILD_MINT,
        description = "Banishes weariness on long marches with crisp garden mint."
    );

    val color: Color get() = Color(colorHex)
}

enum class CustomerType(
    val displayName: String,
    val greeting: String,
    val cloakColorHex: Long,
    val tunicColorHex: Long,
    val hatColorHex: Long
) {
    ADVENTURER(
        displayName = "Traveling Ranger",
        greeting = "Good morrow! Heading past the fairy glade—have you a fresh brew?",
        cloakColorHex = 0xFF15803D,
        tunicColorHex = 0xFF78350F,
        hatColorHex = 0xFF166534
    ),
    APPRENTICE_MAGE(
        displayName = "Arcane Apprentice",
        greeting = "Starlight blessings, keeper! My spellbook studies need a boost.",
        cloakColorHex = 0xFF6D28D9,
        tunicColorHex = 0xFF4C1D95,
        hatColorHex = 0xFF7C3AED
    ),
    TOWN_GUARD(
        displayName = "Royal Guard",
        greeting = "Hail! Long watch at the castle gate today—what's simmering?",
        cloakColorHex = 0xFF1E3A8A,
        tunicColorHex = 0xFF64748B,
        hatColorHex = 0xFF94A3B8
    )
}

enum class CustomerPhase {
    APPROACHING,       // Walking from door to counter
    WAITING_FOR_ORDER, // At counter, order bubble + patience timer shown
    SERVED_LEAVING,    // Happy customer leaving with potion in hand
    MISSED_LEAVING     // Disappointed customer leaving empty-handed
}

data class Customer(
    val id: Long,
    val type: CustomerType,
    val requestedPotion: PotionType,
    val gridX: Float,
    val gridY: Float,
    val phase: CustomerPhase = CustomerPhase.APPROACHING,
    val patienceRemaining: Float = 1.0f
)

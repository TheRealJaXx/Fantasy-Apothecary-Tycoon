package com.example.model

enum class BrewingStep {
    SELECT_RECIPE,
    ADD_INGREDIENTS,
    STIRRING,
    READY_TO_BOTTLE
}

data class PotionRecipe(
    val potionType: PotionType,
    val primaryIngredient: IngredientType,
    val secondaryNotes: List<String>,
    val description: String,
    val requiredStirs: Int = 3
)

object RecipeBook {
    val recipes = listOf(
        PotionRecipe(
            potionType = PotionType.HEALTH_POTION,
            primaryIngredient = IngredientType.SUNBLOOM_PETAL,
            secondaryNotes = listOf("1x Sunbloom Petal (from Pantry)", "Starlight Spring Water"),
            description = "A glowing rose-ruby elixir that restores an adventurer's vitality."
        ),
        PotionRecipe(
            potionType = PotionType.MANA_POTION,
            primaryIngredient = IngredientType.SAPPHIRE_DEW,
            secondaryNotes = listOf("1x Sapphire Dew (from Pantry)", "Moonbeam Mist"),
            description = "A shimmering sky-blue draught prized by arcane apprentices."
        ),
        PotionRecipe(
            potionType = PotionType.STAMINA_POTION,
            primaryIngredient = IngredientType.WILD_MINT,
            secondaryNotes = listOf("1x Wild Mint Leaf (from Pantry)", "Clover Blossom Honey"),
            description = "An emerald herbal tonic that keeps guards energetic on long patrols."
        )
    )

    fun forType(type: PotionType): PotionRecipe {
        return recipes.firstOrNull { it.potionType == type } ?: recipes.first()
    }
}

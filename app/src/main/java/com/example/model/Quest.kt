package com.example.model

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetGoal: Int,
    val rewardGold: Int,
    val rewardBonusIngredient: IngredientType? = null,
    val rewardBonusQty: Int = 0,
    val isClaimed: Boolean = false
) {
    val isCompleted: Boolean get() = currentProgress >= targetGoal
    val progressRatio: Float get() = (currentProgress.toFloat() / targetGoal.toFloat()).coerceIn(0f, 1f)
}

object DefaultQuests {
    fun createInitialQuests(): List<Quest> = listOf(
        Quest(
            id = "quest_morning_prep",
            title = "Cottage Gathering",
            description = "Buy or forage 3 raw ingredients during Morning Prep.",
            currentProgress = 0,
            targetGoal = 3,
            rewardGold = 35,
            rewardBonusIngredient = IngredientType.SUNBLOOM_PETAL,
            rewardBonusQty = 2
        ),
        Quest(
            id = "quest_brew_health",
            title = "Healer's Bounty",
            description = "Brew 2 Health Elixirs in your starlight cauldron.",
            currentProgress = 0,
            targetGoal = 2,
            rewardGold = 50,
            rewardBonusIngredient = IngredientType.SAPPHIRE_DEW,
            rewardBonusQty = 1
        ),
        Quest(
            id = "quest_serve_patrons",
            title = "Welcoming Counter",
            description = "Serve 3 traveling D&D adventurers at your counter.",
            currentProgress = 0,
            targetGoal = 3,
            rewardGold = 65,
            rewardBonusIngredient = IngredientType.WILD_MINT,
            rewardBonusQty = 2
        ),
        Quest(
            id = "quest_earn_gold",
            title = "Starlight Prosperity",
            description = "Earn 60 Gold from potion sales across the counter.",
            currentProgress = 0,
            targetGoal = 60,
            rewardGold = 45
        )
    )
}

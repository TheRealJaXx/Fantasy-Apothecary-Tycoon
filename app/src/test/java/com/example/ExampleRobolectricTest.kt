package com.example

import android.content.Context
import android.os.Looper
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.model.BrewingStep
import com.example.model.DayPhase
import com.example.model.IngredientType
import com.example.model.PotionType
import com.example.viewmodel.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Fantasy Apothecary Tycoon", appName)
    }

    @Test
    fun `game view model initial state and day phase transitions`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = GameViewModel(savedStateHandle)

        assertEquals(120, viewModel.gold.value)
        assertEquals(1, viewModel.day.value)
        assertEquals(DayPhase.MORNING_PREP, viewModel.dayPhase.value)
        assertEquals(4, viewModel.inventoryCount.value)
        assertEquals(4, viewModel.totalIngredientsCount.value)
        assertFalse(viewModel.isShopOpen.value)
        assertFalse(viewModel.isMenuOpen.value)

        // Open shop
        viewModel.toggleShopOpen()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(DayPhase.SHOP_OPEN, viewModel.dayPhase.value)
        assertTrue(viewModel.isShopOpen.value)

        // Close shop early -> DAY_SUMMARY
        viewModel.toggleShopOpen()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(DayPhase.DAY_SUMMARY, viewModel.dayPhase.value)
        assertFalse(viewModel.isShopOpen.value)

        // Advance to Day 2
        viewModel.startNextDay()
        assertEquals(2, viewModel.day.value)
        assertEquals(DayPhase.MORNING_PREP, viewModel.dayPhase.value)
        assertFalse(viewModel.foragedToday.value)
    }

    @Test
    fun `game view model menu and inspection logic`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = GameViewModel(savedStateHandle)

        viewModel.openMenu()
        assertTrue(viewModel.isMenuOpen.value)
        viewModel.closeMenu()
        assertFalse(viewModel.isMenuOpen.value)

        viewModel.inspectItem("Crystal Ball", "Forecasts daily demand")
        assertEquals("Crystal Ball", viewModel.inspectedItemName.value)
        assertEquals("Forecasts daily demand", viewModel.inspectedItemDescription.value)

        viewModel.clearInspection()
        assertNull(viewModel.inspectedItemName.value)
        assertNull(viewModel.inspectedItemDescription.value)
    }

    @Test
    fun `game view model morning forage and ingredient market`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = GameViewModel(savedStateHandle)

        val startHerbs = viewModel.totalIngredientsCount.value
        val startGold = viewModel.gold.value

        // Forage once per morning (+1 of each of the 3 herbs)
        viewModel.forageGarden()
        assertTrue(viewModel.foragedToday.value)
        assertEquals(startHerbs + 3, viewModel.totalIngredientsCount.value)

        // Second forage in same day should be ignored
        viewModel.forageGarden()
        assertEquals(startHerbs + 3, viewModel.totalIngredientsCount.value)

        // Buy 1 Sunbloom Petal at today's forecast price
        val petalPrice = viewModel.dailyForecast.value.ingredientPrices[IngredientType.SUNBLOOM_PETAL] ?: 10
        val beforePetals = viewModel.ingredientPantry.value[IngredientType.SUNBLOOM_PETAL] ?: 0
        viewModel.buyIngredient(IngredientType.SUNBLOOM_PETAL, 1)
        assertEquals(startGold - petalPrice, viewModel.gold.value)
        assertEquals(beforePetals + 1, viewModel.ingredientPantry.value[IngredientType.SUNBLOOM_PETAL])

        // Buy Trio Bundle
        val bundleCost = IngredientType.values().sumOf {
            viewModel.dailyForecast.value.ingredientPrices[it] ?: it.basePrice
        }
        val goldBeforeBundle = viewModel.gold.value
        val herbsBeforeBundle = viewModel.totalIngredientsCount.value
        viewModel.buyStarterBundle()
        assertEquals(goldBeforeBundle - bundleCost, viewModel.gold.value)
        assertEquals(herbsBeforeBundle + 3, viewModel.totalIngredientsCount.value)
    }

    @Test
    fun `game view model potion brewing and quick brew consume pantry ingredients`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = GameViewModel(savedStateHandle)

        val initialStock = viewModel.inventoryCount.value
        val initialDew = viewModel.ingredientPantry.value[IngredientType.SAPPHIRE_DEW] ?: 0
        val initialMana = viewModel.potionStock.value[PotionType.MANA_POTION] ?: 0

        // 1. Open brewing station and select Mana Potion
        viewModel.openBrewingStation(PotionType.HEALTH_POTION)
        assertTrue(viewModel.isBrewingOpen.value)
        viewModel.selectBrewPotion(PotionType.MANA_POTION)
        assertEquals(PotionType.MANA_POTION.name, viewModel.selectedBrewPotion.value)

        // 2. Add ingredients (consumes 1 Sapphire Dew)
        viewModel.addIngredients()
        assertEquals(BrewingStep.STIRRING.name, viewModel.brewingStep.value)
        assertEquals(initialDew - 1, viewModel.ingredientPantry.value[IngredientType.SAPPHIRE_DEW])

        // 3. Stir cauldron 3 times
        viewModel.stirCauldron()
        viewModel.stirCauldron()
        viewModel.stirCauldron()
        assertEquals(3, viewModel.stirCount.value)
        assertEquals(BrewingStep.READY_TO_BOTTLE.name, viewModel.brewingStep.value)

        // 4. Bottle potion
        viewModel.bottlePotion()
        assertEquals(initialStock + 1, viewModel.inventoryCount.value)
        assertEquals(initialMana + 1, viewModel.potionStock.value[PotionType.MANA_POTION])

        // 5. Quick brew Health Elixir
        val initialPetals = viewModel.ingredientPantry.value[IngredientType.SUNBLOOM_PETAL] ?: 0
        val initialHealth = viewModel.potionStock.value[PotionType.HEALTH_POTION] ?: 0
        val quickOk = viewModel.quickBrewPotion(PotionType.HEALTH_POTION)
        assertTrue(quickOk)
        assertEquals(initialPetals - 1, viewModel.ingredientPantry.value[IngredientType.SUNBLOOM_PETAL])
        assertEquals(initialHealth + 1, viewModel.potionStock.value[PotionType.HEALTH_POTION])

        viewModel.closeBrewingStation()
        assertFalse(viewModel.isBrewingOpen.value)
    }

    @Test
    fun `game view model daily quests progress and claim reward`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = GameViewModel(savedStateHandle)

        val initialGold = viewModel.gold.value
        val initialDew = viewModel.ingredientPantry.value[IngredientType.SAPPHIRE_DEW] ?: 0

        assertEquals(4, viewModel.quests.value.size)

        // Brew 2 Health Elixirs to complete quest_brew_health (we start with 2 Sunbloom Petals)
        viewModel.openBrewingStation(PotionType.HEALTH_POTION)
        viewModel.selectBrewPotion(PotionType.HEALTH_POTION)

        // First potion
        viewModel.addIngredients()
        viewModel.stirCauldron()
        viewModel.stirCauldron()
        viewModel.stirCauldron()
        viewModel.bottlePotion()

        // Second potion
        viewModel.addIngredients()
        viewModel.stirCauldron()
        viewModel.stirCauldron()
        viewModel.stirCauldron()
        viewModel.bottlePotion()

        val healthQuest = viewModel.quests.value.first { it.id == "quest_brew_health" }
        assertEquals(2, healthQuest.currentProgress)
        assertTrue(healthQuest.isCompleted)
        assertFalse(healthQuest.isClaimed)

        // Claim reward (+50 gold, +1 Sapphire Dew)
        viewModel.claimQuestReward("quest_brew_health")

        val claimedQuest = viewModel.quests.value.first { it.id == "quest_brew_health" }
        assertTrue(claimedQuest.isClaimed)
        assertEquals(initialGold + 50, viewModel.gold.value)
        assertEquals(initialDew + 1, viewModel.ingredientPantry.value[IngredientType.SAPPHIRE_DEW])
    }
}

package com.example.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.BrewingStep
import com.example.model.Customer
import com.example.model.CustomerPhase
import com.example.model.CustomerType
import com.example.model.DailyForecast
import com.example.model.DailySummary
import com.example.model.DayPhase
import com.example.model.DefaultQuests
import com.example.model.ForecastGenerator
import com.example.model.IngredientType
import com.example.model.PotionType
import com.example.model.Quest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        const val MAX_SHELF_CAPACITY = 24

        private const val KEY_GOLD = "key_gold"
        private const val KEY_DAY = "key_day"
        private const val KEY_DAY_PHASE = "key_day_phase"
        private const val KEY_FORAGED_TODAY = "key_foraged_today"

        private const val KEY_IS_MENU_OPEN = "key_is_menu_open"
        private const val KEY_IS_BREWING_OPEN = "key_is_brewing_open"
        private const val KEY_IS_QUESTS_OPEN = "key_is_quests_open"
        private const val KEY_IS_MARKET_OPEN = "key_is_market_open"
        private const val KEY_IS_CRYSTAL_BALL_OPEN = "key_is_crystal_ball_open"

        private const val KEY_STATUS_MESSAGE = "key_status_message"
        private const val KEY_INSPECTED_NAME = "key_inspected_name"
        private const val KEY_INSPECTED_DESC = "key_inspected_desc"
        private const val KEY_SERVED_TOTAL = "key_served_count"

        private const val KEY_SERVED_TODAY = "key_served_today"
        private const val KEY_MISSED_TODAY = "key_missed_today"
        private const val KEY_EARNED_TODAY = "key_earned_today"
        private const val KEY_SPENT_TODAY = "key_spent_today"
        private const val KEY_WAVE_INDEX = "key_wave_index"

        private const val KEY_BREW_POTION = "key_brew_potion"
        private const val KEY_BREWING_STEP = "key_brewing_step"
        private const val KEY_STIR_COUNT = "key_stir_count"
        private const val KEY_CAULDRON_COLOR = "key_cauldron_color"

        // Positions in isometric floor coordinates
        private const val DOOR_X = 4.4f
        private const val DOOR_Y = 0.6f
        private const val COUNTER_X = 4.2f
        private const val COUNTER_Y = 2.7f
    }

    val gold: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_GOLD, 120)
    val day: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_DAY, 1)
    val dayPhaseName: StateFlow<String> = savedStateHandle.getStateFlow(KEY_DAY_PHASE, DayPhase.MORNING_PREP.name)
    val dayPhase: StateFlow<DayPhase> = dayPhaseName.map {
        try { DayPhase.valueOf(it) } catch (_: Exception) { DayPhase.MORNING_PREP }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, DayPhase.MORNING_PREP)

    val foragedToday: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_FORAGED_TODAY, false)

    val isMenuOpen: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_IS_MENU_OPEN, false)
    val isBrewingOpen: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_IS_BREWING_OPEN, false)
    val isQuestsOpen: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_IS_QUESTS_OPEN, false)
    val isMarketOpen: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_IS_MARKET_OPEN, false)
    val isCrystalBallOpen: StateFlow<Boolean> = savedStateHandle.getStateFlow(KEY_IS_CRYSTAL_BALL_OPEN, false)

    val statusMessage: StateFlow<String> = savedStateHandle.getStateFlow(
        KEY_STATUS_MESSAGE,
        "Morning Prep: Peek at the Crystal Ball, buy or forage herbs, and brew potions before opening!"
    )
    val inspectedItemName: StateFlow<String?> = savedStateHandle.getStateFlow(KEY_INSPECTED_NAME, null)
    val inspectedItemDescription: StateFlow<String?> = savedStateHandle.getStateFlow(KEY_INSPECTED_DESC, null)
    val totalCustomersServed: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_SERVED_TOTAL, 0)

    val customersServedToday: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_SERVED_TODAY, 0)
    val customersMissedToday: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_MISSED_TODAY, 0)
    val goldEarnedToday: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_EARNED_TODAY, 0)
    val goldSpentToday: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_SPENT_TODAY, 0)
    val waveIndex: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_WAVE_INDEX, 0)

    val selectedBrewPotion: StateFlow<String> = savedStateHandle.getStateFlow(
        KEY_BREW_POTION,
        PotionType.HEALTH_POTION.name
    )
    val brewingStep: StateFlow<String> = savedStateHandle.getStateFlow(
        KEY_BREWING_STEP,
        BrewingStep.SELECT_RECIPE.name
    )
    val stirCount: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_STIR_COUNT, 0)
    val cauldronColorHex: StateFlow<Long> = savedStateHandle.getStateFlow(
        KEY_CAULDRON_COLOR,
        0xFFF43F5E
    )

    // Per-potion bottle stock on shop shelves
    private val _potionStock = MutableStateFlow(loadPotionStock())
    val potionStock: StateFlow<Map<PotionType, Int>> = _potionStock.asStateFlow()

    val inventoryCount: StateFlow<Int> = _potionStock.map { map ->
        map.values.sum()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, loadPotionStock().values.sum())

    // Raw ingredient pantry stock
    private val _ingredientPantry = MutableStateFlow(loadIngredientPantry())
    val ingredientPantry: StateFlow<Map<IngredientType, Int>> = _ingredientPantry.asStateFlow()

    val totalIngredientsCount: StateFlow<Int> = _ingredientPantry.map { map ->
        map.values.sum()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, loadIngredientPantry().values.sum())

    val dailyForecast: StateFlow<DailyForecast> = day.map { currentDay ->
        ForecastGenerator.generateForDay(currentDay)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ForecastGenerator.generateForDay(day.value))

    val isShopOpen: StateFlow<Boolean> = dayPhase.map { it == DayPhase.SHOP_OPEN }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val dailySummary: StateFlow<DailySummary> = combine(
        day,
        customersServedToday,
        customersMissedToday,
        goldEarnedToday,
        goldSpentToday,
        foragedToday
    ) { values ->
        DailySummary(
            dayNumber = values[0] as Int,
            customersServed = values[1] as Int,
            customersMissed = values[2] as Int,
            goldEarned = values[3] as Int,
            goldSpent = values[4] as Int,
            foragedToday = values[5] as Boolean
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        DailySummary(1, 0, 0, 0, 0, false)
    )

    private val _currentCustomer = MutableStateFlow<Customer?>(null)
    val currentCustomer: StateFlow<Customer?> = _currentCustomer.asStateFlow()

    private val _recentSaleNotification = MutableStateFlow<String?>(null)
    val recentSaleNotification: StateFlow<String?> = _recentSaleNotification.asStateFlow()

    private val _quests = MutableStateFlow<List<Quest>>(loadQuests())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    val unclaimedQuestsCount: StateFlow<Int> = _quests.map { list ->
        list.count { it.isCompleted && !it.isClaimed }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private var customerWaveJob: Job? = null
    private var customerSequenceId = 1L

    init {
        if (dayPhase.value == DayPhase.SHOP_OPEN) {
            startCustomerWaveLoop()
        }
    }

    private fun loadPotionStock(): Map<PotionType, Int> {
        val defaults = mapOf(
            PotionType.HEALTH_POTION to 2,
            PotionType.MANA_POTION to 1,
            PotionType.STAMINA_POTION to 1
        )
        return PotionType.values().associateWith { type ->
            savedStateHandle.get<Int>("stock_${type.name}") ?: (defaults[type] ?: 0)
        }
    }

    private fun savePotionStock(updated: Map<PotionType, Int>) {
        updated.forEach { (type, count) ->
            savedStateHandle["stock_${type.name}"] = count
        }
        _potionStock.value = updated
    }

    private fun loadIngredientPantry(): Map<IngredientType, Int> {
        val defaults = mapOf(
            IngredientType.SUNBLOOM_PETAL to 2,
            IngredientType.SAPPHIRE_DEW to 1,
            IngredientType.WILD_MINT to 1
        )
        return IngredientType.values().associateWith { type ->
            savedStateHandle.get<Int>("pantry_${type.name}") ?: (defaults[type] ?: 0)
        }
    }

    private fun saveIngredientPantry(updated: Map<IngredientType, Int>) {
        updated.forEach { (type, count) ->
            savedStateHandle["pantry_${type.name}"] = count
        }
        _ingredientPantry.value = updated
    }

    private fun loadQuests(): List<Quest> {
        return DefaultQuests.createInitialQuests().map { q ->
            val prog = savedStateHandle.get<Int>("quest_prog_${q.id}") ?: q.currentProgress
            val claimed = savedStateHandle.get<Boolean>("quest_claimed_${q.id}") ?: q.isClaimed
            q.copy(currentProgress = prog, isClaimed = claimed)
        }
    }

    private fun updateQuestProgress(questId: String, delta: Int) {
        val updatedList = _quests.value.map { q ->
            if (q.id == questId && !q.isClaimed) {
                val newProg = (q.currentProgress + delta).coerceAtMost(q.targetGoal)
                savedStateHandle["quest_prog_${q.id}"] = newProg
                q.copy(currentProgress = newProg)
            } else {
                q
            }
        }
        _quests.value = updatedList
    }

    fun claimQuestReward(questId: String) {
        val quest = _quests.value.firstOrNull { it.id == questId } ?: return
        if (!quest.isCompleted || quest.isClaimed) return

        savedStateHandle["quest_claimed_${quest.id}"] = true
        _quests.value = _quests.value.map {
            if (it.id == questId) it.copy(isClaimed = true) else it
        }

        savedStateHandle[KEY_GOLD] = gold.value + quest.rewardGold
        val bonusIng = quest.rewardBonusIngredient
        if (bonusIng != null && quest.rewardBonusQty > 0) {
            val updatedPantry = _ingredientPantry.value.toMutableMap()
            updatedPantry[bonusIng] = (updatedPantry[bonusIng] ?: 0) + quest.rewardBonusQty
            saveIngredientPantry(updatedPantry)
        }

        showFloatingBadge("+${quest.rewardGold} G (Quest!)")
        val bonusText = if (bonusIng != null) " & +${quest.rewardBonusQty} ${bonusIng.displayName}" else ""
        savedStateHandle[KEY_STATUS_MESSAGE] = "Completed '${quest.title}'! Claimed +${quest.rewardGold} G$bonusText!"
    }

    // --- Morning Prep Actions: Forage & Market ---

    fun forageGarden() {
        if (foragedToday.value) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "You already gathered today's wild herbs from the cottage garden!"
            return
        }
        savedStateHandle[KEY_FORAGED_TODAY] = true

        val updatedPantry = _ingredientPantry.value.toMutableMap()
        // Always yields 1 of each herb so foraging feels rewarding and predictable
        IngredientType.values().forEach { ing ->
            updatedPantry[ing] = (updatedPantry[ing] ?: 0) + 1
        }
        saveIngredientPantry(updatedPantry)
        updateQuestProgress("quest_morning_prep", 3)

        showFloatingBadge("+3 Garden Herbs!")
        savedStateHandle[KEY_STATUS_MESSAGE] =
            "Foraged 1 Sunbloom Petal, 1 Sapphire Dew, and 1 Wild Mint from your cottage garden!"
    }

    fun buyIngredient(type: IngredientType, quantity: Int = 1) {
        val unitPrice = dailyForecast.value.ingredientPrices[type] ?: type.basePrice
        val totalCost = unitPrice * quantity

        if (gold.value < totalCost) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Not enough Gold! Need $totalCost G for ${quantity}x ${type.displayName}."
            return
        }

        savedStateHandle[KEY_GOLD] = gold.value - totalCost
        savedStateHandle[KEY_SPENT_TODAY] = goldSpentToday.value + totalCost

        val updatedPantry = _ingredientPantry.value.toMutableMap()
        updatedPantry[type] = (updatedPantry[type] ?: 0) + quantity
        saveIngredientPantry(updatedPantry)

        updateQuestProgress("quest_morning_prep", quantity)
        savedStateHandle[KEY_STATUS_MESSAGE] = "Bought ${quantity}x ${type.displayName} for $totalCost G."
    }

    fun buyStarterBundle() {
        val prices = dailyForecast.value.ingredientPrices
        val bundleCost = IngredientType.values().sumOf { prices[it] ?: it.basePrice }

        if (gold.value < bundleCost) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Not enough Gold for the trio bundle ($bundleCost G)."
            return
        }

        savedStateHandle[KEY_GOLD] = gold.value - bundleCost
        savedStateHandle[KEY_SPENT_TODAY] = goldSpentToday.value + bundleCost

        val updatedPantry = _ingredientPantry.value.toMutableMap()
        IngredientType.values().forEach { ing ->
            updatedPantry[ing] = (updatedPantry[ing] ?: 0) + 1
        }
        saveIngredientPantry(updatedPantry)
        updateQuestProgress("quest_morning_prep", 3)

        showFloatingBadge("Bought Trio Bundle!")
        savedStateHandle[KEY_STATUS_MESSAGE] = "Stocked 1 of each cottage ingredient for $bundleCost G!"
    }

    // --- Day Phase Transitions ---

    fun toggleShopOpen() {
        when (dayPhase.value) {
            DayPhase.MORNING_PREP -> startDayShift()
            DayPhase.SHOP_OPEN -> finishDayEarly()
            DayPhase.DAY_SUMMARY -> startNextDay()
        }
    }

    private fun startDayShift() {
        savedStateHandle[KEY_DAY_PHASE] = DayPhase.SHOP_OPEN.name
        savedStateHandle[KEY_WAVE_INDEX] = 0
        savedStateHandle[KEY_IS_MARKET_OPEN] = false
        savedStateHandle[KEY_IS_CRYSTAL_BALL_OPEN] = false
        savedStateHandle[KEY_STATUS_MESSAGE] =
            "Day ${day.value} Open! Hope you brewed enough—${dailyForecast.value.expectedCustomers} adventurers are on their way!"
        startCustomerWaveLoop()
    }

    private fun finishDayEarly() {
        customerWaveJob?.cancel()
        customerWaveJob = null
        viewModelScope.launch {
            if (_currentCustomer.value != null) {
                departCustomer(served = false)
            }
            savedStateHandle[KEY_DAY_PHASE] = DayPhase.DAY_SUMMARY.name
            savedStateHandle[KEY_STATUS_MESSAGE] = "Shop closed for the evening. Review your Day ${day.value} Ledger!"
        }
    }

    fun startNextDay() {
        val nextDay = day.value + 1
        savedStateHandle[KEY_DAY] = nextDay
        savedStateHandle[KEY_DAY_PHASE] = DayPhase.MORNING_PREP.name
        savedStateHandle[KEY_FORAGED_TODAY] = false
        savedStateHandle[KEY_SERVED_TODAY] = 0
        savedStateHandle[KEY_MISSED_TODAY] = 0
        savedStateHandle[KEY_EARNED_TODAY] = 0
        savedStateHandle[KEY_SPENT_TODAY] = 0
        savedStateHandle[KEY_WAVE_INDEX] = 0
        _currentCustomer.value = null

        // Reset any claimed quests so the new day gives fresh motivation
        val refreshedQuests = _quests.value.map { q ->
            if (q.isClaimed) {
                savedStateHandle["quest_prog_${q.id}"] = 0
                savedStateHandle["quest_claimed_${q.id}"] = false
                q.copy(currentProgress = 0, isClaimed = false)
            } else {
                q
            }
        }
        _quests.value = refreshedQuests

        val forecast = ForecastGenerator.generateForDay(nextDay)
        savedStateHandle[KEY_STATUS_MESSAGE] =
            "Morning of Day $nextDay! Crystal Ball: '${forecast.eventTitle}'. Buy or forage supplies before opening!"
    }

    // --- Customer Wave Loop ---

    private fun pickWeightedPotion(forecast: DailyForecast): PotionType {
        val roll = Random.nextInt(100)
        return when {
            roll < forecast.healthDemandPct -> PotionType.HEALTH_POTION
            roll < forecast.healthDemandPct + forecast.manaDemandPct -> PotionType.MANA_POTION
            else -> PotionType.STAMINA_POTION
        }
    }

    private fun pickCustomerForPotion(potion: PotionType): CustomerType {
        return when (potion) {
            PotionType.HEALTH_POTION -> if (Random.nextBoolean()) CustomerType.ADVENTURER else CustomerType.TOWN_GUARD
            PotionType.MANA_POTION -> CustomerType.APPRENTICE_MAGE
            PotionType.STAMINA_POTION -> if (Random.nextBoolean()) CustomerType.TOWN_GUARD else CustomerType.ADVENTURER
        }
    }

    private fun startCustomerWaveLoop() {
        customerWaveJob?.cancel()
        customerWaveJob = viewModelScope.launch {
            val forecast = dailyForecast.value
            val totalVisitors = forecast.expectedCustomers

            delay(900)

            while (dayPhase.value == DayPhase.SHOP_OPEN && waveIndex.value < totalVisitors) {
                val nextNum = waveIndex.value + 1
                savedStateHandle[KEY_WAVE_INDEX] = nextNum

                val requestedPotion = pickWeightedPotion(forecast)
                val customerType = pickCustomerForPotion(requestedPotion)
                val newCust = Customer(
                    id = customerSequenceId++,
                    type = customerType,
                    requestedPotion = requestedPotion,
                    gridX = DOOR_X,
                    gridY = DOOR_Y,
                    phase = CustomerPhase.APPROACHING,
                    patienceRemaining = 1.0f
                )
                _currentCustomer.value = newCust
                savedStateHandle[KEY_STATUS_MESSAGE] =
                    "Patron $nextNum/$totalVisitors: ${newCust.type.displayName} stepped inside the cottage!"

                // Walk from door to counter
                val steps = 14
                for (i in 1..steps) {
                    if (dayPhase.value != DayPhase.SHOP_OPEN) return@launch
                    delay(65)
                    val progress = i.toFloat() / steps
                    val curX = DOOR_X + (COUNTER_X - DOOR_X) * progress
                    val curY = DOOR_Y + (COUNTER_Y - DOOR_Y) * progress
                    _currentCustomer.value = _currentCustomer.value?.copy(gridX = curX, gridY = curY)
                }

                // Wait at counter with patience countdown (~22 seconds)
                _currentCustomer.value = _currentCustomer.value?.copy(
                    gridX = COUNTER_X,
                    gridY = COUNTER_Y,
                    phase = CustomerPhase.WAITING_FOR_ORDER,
                    patienceRemaining = 1.0f
                )
                savedStateHandle[KEY_STATUS_MESSAGE] =
                    "${newCust.type.displayName}: '${newCust.type.greeting} One ${newCust.requestedPotion.displayName}, please!'"

                while (dayPhase.value == DayPhase.SHOP_OPEN &&
                    _currentCustomer.value?.phase == CustomerPhase.WAITING_FOR_ORDER
                ) {
                    delay(250)
                    val active = _currentCustomer.value ?: break
                    if (active.phase != CustomerPhase.WAITING_FOR_ORDER) break

                    val nextPatience = (active.patienceRemaining - 0.0115f).coerceAtLeast(0f)
                    if (nextPatience <= 0f) {
                        // Patience ran out -> Missed sale!
                        savedStateHandle[KEY_MISSED_TODAY] = customersMissedToday.value + 1
                        savedStateHandle[KEY_STATUS_MESSAGE] =
                            "Oh dear! ${active.type.displayName} ran out of patience waiting for ${active.requestedPotion.displayName}!"
                        showFloatingBadge("Missed Sale!")
                        departCustomer(served = false)
                        break
                    } else {
                        _currentCustomer.value = active.copy(patienceRemaining = nextPatience)
                    }
                }

                // Wait until departing animation completes
                while (_currentCustomer.value != null && dayPhase.value == DayPhase.SHOP_OPEN) {
                    delay(120)
                }

                if (dayPhase.value == DayPhase.SHOP_OPEN && waveIndex.value < totalVisitors) {
                    delay(1400)
                }
            }

            // All visitors for today finished -> transition to End-of-Day Summary
            if (dayPhase.value == DayPhase.SHOP_OPEN) {
                savedStateHandle[KEY_DAY_PHASE] = DayPhase.DAY_SUMMARY.name
                savedStateHandle[KEY_STATUS_MESSAGE] =
                    "Evening starlight falls! Day ${day.value} is complete—check your ledger."
            }
        }
    }

    fun serveCurrentCustomer() {
        val cust = _currentCustomer.value ?: return
        if (cust.phase != CustomerPhase.WAITING_FOR_ORDER) return

        val potion = cust.requestedPotion
        val currentMap = _potionStock.value
        val available = currentMap[potion] ?: 0

        if (available <= 0) {
            val ing = potion.requiredIngredient
            val ingCount = _ingredientPantry.value[ing] ?: 0
            savedStateHandle[KEY_STATUS_MESSAGE] = if (ingCount > 0) {
                "No ${potion.displayName} bottled! Tap 'Quick Brew' or open the Cauldron to brew one fast!"
            } else {
                "Out of ${potion.displayName} AND ${ing.displayName}! Use Pixie Express before they leave!"
            }
            return
        }

        // Deduct matching potion & credit gold
        val updatedStock = currentMap.toMutableMap()
        updatedStock[potion] = available - 1
        savePotionStock(updatedStock)

        val earnedGold = potion.price
        savedStateHandle[KEY_GOLD] = gold.value + earnedGold
        savedStateHandle[KEY_EARNED_TODAY] = goldEarnedToday.value + earnedGold
        savedStateHandle[KEY_SERVED_TOTAL] = totalCustomersServed.value + 1
        savedStateHandle[KEY_SERVED_TODAY] = customersServedToday.value + 1

        updateQuestProgress("quest_serve_patrons", 1)
        updateQuestProgress("quest_earn_gold", earnedGold)

        showFloatingBadge("+${earnedGold} G!")
        savedStateHandle[KEY_STATUS_MESSAGE] =
            "Sold ${potion.displayName} to ${cust.type.displayName} for +$earnedGold G!"

        viewModelScope.launch {
            departCustomer(served = true)
        }
    }

    fun pixieExpressServeCurrentCustomer() {
        val cust = _currentCustomer.value ?: return
        if (cust.phase != CustomerPhase.WAITING_FOR_ORDER) return

        val potion = cust.requestedPotion
        val marketPrice = dailyForecast.value.ingredientPrices[potion.requiredIngredient]
            ?: potion.requiredIngredient.basePrice
        val emergencyFee = marketPrice * 2

        if (gold.value < emergencyFee) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Not enough Gold for Pixie Express ($emergencyFee G)!"
            return
        }

        // Pay emergency courier fee & immediately fulfill customer order
        savedStateHandle[KEY_GOLD] = gold.value - emergencyFee + potion.price
        savedStateHandle[KEY_SPENT_TODAY] = goldSpentToday.value + emergencyFee
        savedStateHandle[KEY_EARNED_TODAY] = goldEarnedToday.value + potion.price
        savedStateHandle[KEY_SERVED_TOTAL] = totalCustomersServed.value + 1
        savedStateHandle[KEY_SERVED_TODAY] = customersServedToday.value + 1

        updateQuestProgress("quest_serve_patrons", 1)
        updateQuestProgress("quest_earn_gold", potion.price)

        val net = potion.price - emergencyFee
        showFloatingBadge("Pixie Express! (+$net G net)")
        savedStateHandle[KEY_STATUS_MESSAGE] =
            "Pixie Express rushed a ${potion.displayName} (-$emergencyFee G fee, +${potion.price} G sale)!"

        viewModelScope.launch {
            departCustomer(served = true)
        }
    }

    private suspend fun departCustomer(served: Boolean) {
        val cust = _currentCustomer.value ?: return
        _currentCustomer.value = cust.copy(
            phase = if (served) CustomerPhase.SERVED_LEAVING else CustomerPhase.MISSED_LEAVING
        )

        val steps = 13
        val startX = cust.gridX
        val startY = cust.gridY
        for (i in 1..steps) {
            delay(55)
            val progress = i.toFloat() / steps
            val curX = startX + (DOOR_X - startX) * progress
            val curY = startY + (DOOR_Y - startY) * progress
            _currentCustomer.value = _currentCustomer.value?.copy(gridX = curX, gridY = curY)
        }
        _currentCustomer.value = null
    }

    // --- Brewing Station Actions ---

    fun openBrewingStation(type: PotionType = PotionType.HEALTH_POTION) {
        savedStateHandle[KEY_IS_BREWING_OPEN] = true
        savedStateHandle[KEY_BREW_POTION] = type.name
        savedStateHandle[KEY_BREWING_STEP] = BrewingStep.SELECT_RECIPE.name
        savedStateHandle[KEY_STIR_COUNT] = 0
    }

    fun closeBrewingStation() {
        savedStateHandle[KEY_IS_BREWING_OPEN] = false
    }

    fun selectBrewPotion(type: PotionType) {
        savedStateHandle[KEY_BREW_POTION] = type.name
        savedStateHandle[KEY_BREWING_STEP] = BrewingStep.SELECT_RECIPE.name
        savedStateHandle[KEY_STIR_COUNT] = 0
    }

    fun addIngredients() {
        val potion = getSelectedPotion()
        val reqIng = potion.requiredIngredient
        val pantryMap = _ingredientPantry.value
        val availableIng = pantryMap[reqIng] ?: 0

        if (availableIng <= 0) {
            savedStateHandle[KEY_STATUS_MESSAGE] =
                "You need 1x ${reqIng.displayName}! Visit the Morning Market or Forage Garden."
            return
        }

        if (inventoryCount.value >= MAX_SHELF_CAPACITY) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Shelves are full ($MAX_SHELF_CAPACITY/$MAX_SHELF_CAPACITY bottles)!"
            return
        }

        val updatedPantry = pantryMap.toMutableMap()
        updatedPantry[reqIng] = availableIng - 1
        saveIngredientPantry(updatedPantry)

        savedStateHandle[KEY_BREWING_STEP] = BrewingStep.STIRRING.name
        savedStateHandle[KEY_STIR_COUNT] = 0
        savedStateHandle[KEY_CAULDRON_COLOR] = potion.colorHex
        savedStateHandle[KEY_STATUS_MESSAGE] =
            "Dropped 1x ${reqIng.displayName} into the cauldron! Stir 3 times to infuse starlight."
    }

    fun stirCauldron() {
        val currentStir = stirCount.value + 1
        savedStateHandle[KEY_STIR_COUNT] = currentStir

        if (currentStir >= 3) {
            savedStateHandle[KEY_BREWING_STEP] = BrewingStep.READY_TO_BOTTLE.name
            savedStateHandle[KEY_STATUS_MESSAGE] = "The starlight brew shimmers! Ready to bottle into a crystal flask."
        } else {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Stirring the shimmering cauldron... ($currentStir/3)"
        }
    }

    fun bottlePotion() {
        val potion = getSelectedPotion()
        if (inventoryCount.value >= MAX_SHELF_CAPACITY) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Your cottage shelves are full ($MAX_SHELF_CAPACITY/$MAX_SHELF_CAPACITY)!"
            return
        }

        val updatedStock = _potionStock.value.toMutableMap()
        updatedStock[potion] = (updatedStock[potion] ?: 0) + 1
        savePotionStock(updatedStock)

        savedStateHandle[KEY_BREWING_STEP] = BrewingStep.SELECT_RECIPE.name
        savedStateHandle[KEY_STIR_COUNT] = 0

        if (potion == PotionType.HEALTH_POTION) {
            updateQuestProgress("quest_brew_health", 1)
        }

        showFloatingBadge("+1 ${potion.shortName}!")
        savedStateHandle[KEY_STATUS_MESSAGE] = "Bottled 1x ${potion.displayName} and placed it on the shelf!"
    }

    fun quickBrewPotion(potion: PotionType): Boolean {
        if (inventoryCount.value >= MAX_SHELF_CAPACITY) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Shelves are full ($MAX_SHELF_CAPACITY/$MAX_SHELF_CAPACITY)!"
            return false
        }
        val reqIng = potion.requiredIngredient
        val pantryMap = _ingredientPantry.value
        val available = pantryMap[reqIng] ?: 0
        if (available <= 0) {
            savedStateHandle[KEY_STATUS_MESSAGE] = "Out of ${reqIng.displayName} in pantry!"
            return false
        }

        val updatedPantry = pantryMap.toMutableMap()
        updatedPantry[reqIng] = available - 1
        saveIngredientPantry(updatedPantry)

        val updatedStock = _potionStock.value.toMutableMap()
        updatedStock[potion] = (updatedStock[potion] ?: 0) + 1
        savePotionStock(updatedStock)

        savedStateHandle[KEY_CAULDRON_COLOR] = potion.colorHex
        if (potion == PotionType.HEALTH_POTION) {
            updateQuestProgress("quest_brew_health", 1)
        }

        showFloatingBadge("+1 ${potion.shortName} Brewed!")
        savedStateHandle[KEY_STATUS_MESSAGE] = "Quick-brewed 1x ${potion.displayName} from 1x ${reqIng.displayName}!"
        return true
    }

    fun getSelectedPotion(): PotionType {
        return try {
            PotionType.valueOf(selectedBrewPotion.value)
        } catch (_: Exception) {
            PotionType.HEALTH_POTION
        }
    }

    private fun showFloatingBadge(text: String) {
        _recentSaleNotification.value = text
        viewModelScope.launch {
            delay(1800)
            if (_recentSaleNotification.value == text) {
                _recentSaleNotification.value = null
            }
        }
    }

    // --- Dialog Visibility ---

    fun openMenu() { savedStateHandle[KEY_IS_MENU_OPEN] = true }
    fun closeMenu() { savedStateHandle[KEY_IS_MENU_OPEN] = false }

    fun openQuestsDialog() { savedStateHandle[KEY_IS_QUESTS_OPEN] = true }
    fun closeQuestsDialog() { savedStateHandle[KEY_IS_QUESTS_OPEN] = false }

    fun openMarketDialog() { savedStateHandle[KEY_IS_MARKET_OPEN] = true }
    fun closeMarketDialog() { savedStateHandle[KEY_IS_MARKET_OPEN] = false }

    fun openCrystalBallDialog() { savedStateHandle[KEY_IS_CRYSTAL_BALL_OPEN] = true }
    fun closeCrystalBallDialog() { savedStateHandle[KEY_IS_CRYSTAL_BALL_OPEN] = false }

    fun inspectItem(name: String, description: String) {
        savedStateHandle[KEY_INSPECTED_NAME] = name
        savedStateHandle[KEY_INSPECTED_DESC] = description
    }

    fun clearInspection() {
        savedStateHandle[KEY_INSPECTED_NAME] = null
        savedStateHandle[KEY_INSPECTED_DESC] = null
    }
}

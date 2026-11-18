package com.example.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BrewingStep
import com.example.model.Customer
import com.example.model.CustomerPhase
import com.example.model.DailyForecast
import com.example.model.DayPhase
import com.example.model.IngredientType
import com.example.model.PotionType
import com.example.ui.render.ApothecaryShopCanvas
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ButtercreamGold
import com.example.ui.theme.LightAmber
import com.example.ui.theme.OnSurfaceMuted
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelRose
import com.example.ui.theme.PastelSky
import com.example.ui.theme.SoftCream
import com.example.ui.theme.StarlightIndigo
import com.example.ui.theme.StarlightMist
import com.example.ui.theme.StarlightPlum
import com.example.ui.theme.StarlightTwilight
import com.example.viewmodel.GameViewModel

@Composable
fun ApothecaryGameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val gold by viewModel.gold.collectAsStateWithLifecycle()
    val day by viewModel.day.collectAsStateWithLifecycle()
    val dayPhase by viewModel.dayPhase.collectAsStateWithLifecycle()
    val isShopOpen by viewModel.isShopOpen.collectAsStateWithLifecycle()

    val potionStock by viewModel.potionStock.collectAsStateWithLifecycle()
    val inventoryCount by viewModel.inventoryCount.collectAsStateWithLifecycle()
    val ingredientPantry by viewModel.ingredientPantry.collectAsStateWithLifecycle()
    val totalIngredientsCount by viewModel.totalIngredientsCount.collectAsStateWithLifecycle()
    val dailyForecast by viewModel.dailyForecast.collectAsStateWithLifecycle()
    val foragedToday by viewModel.foragedToday.collectAsStateWithLifecycle()
    val dailySummary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val waveIndex by viewModel.waveIndex.collectAsStateWithLifecycle()

    val isMenuOpen by viewModel.isMenuOpen.collectAsStateWithLifecycle()
    val isBrewingOpen by viewModel.isBrewingOpen.collectAsStateWithLifecycle()
    val isQuestsOpen by viewModel.isQuestsOpen.collectAsStateWithLifecycle()
    val isMarketOpen by viewModel.isMarketOpen.collectAsStateWithLifecycle()
    val isCrystalBallOpen by viewModel.isCrystalBallOpen.collectAsStateWithLifecycle()

    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val inspectedName by viewModel.inspectedItemName.collectAsStateWithLifecycle()
    val inspectedDesc by viewModel.inspectedItemDescription.collectAsStateWithLifecycle()
    val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    val recentSale by viewModel.recentSaleNotification.collectAsStateWithLifecycle()
    val totalServed by viewModel.totalCustomersServed.collectAsStateWithLifecycle()

    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val unclaimedQuestsCount by viewModel.unclaimedQuestsCount.collectAsStateWithLifecycle()

    val selectedPotionRaw by viewModel.selectedBrewPotion.collectAsStateWithLifecycle()
    val brewingStepRaw by viewModel.brewingStep.collectAsStateWithLifecycle()
    val stirCount by viewModel.stirCount.collectAsStateWithLifecycle()
    val cauldronColorHex by viewModel.cauldronColorHex.collectAsStateWithLifecycle()

    val selectedPotion = try {
        PotionType.valueOf(selectedPotionRaw)
    } catch (_: Exception) {
        PotionType.HEALTH_POTION
    }

    val brewingStep = try {
        BrewingStep.valueOf(brewingStepRaw)
    } catch (_: Exception) {
        BrewingStep.SELECT_RECIPE
    }

    // Handle back button for open dialogs and item inspection
    BackHandler(
        enabled = isMenuOpen || isBrewingOpen || isQuestsOpen || isMarketOpen ||
            isCrystalBallOpen || inspectedName != null
    ) {
        when {
            isMenuOpen -> viewModel.closeMenu()
            isBrewingOpen -> viewModel.closeBrewingStation()
            isQuestsOpen -> viewModel.closeQuestsDialog()
            isMarketOpen -> viewModel.closeMarketDialog()
            isCrystalBallOpen -> viewModel.closeCrystalBallDialog()
            inspectedName != null -> viewModel.clearInspection()
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLandscape) {
                // Landscape Layout: Isometric Cottage on the left, Controls & Tycoon HUD on the right
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.45f)
                            .fillMaxHeight()
                            .padding(end = 8.dp)
                    ) {
                        ApothecaryShopCanvas(
                            onInspectObject = { name, desc ->
                                viewModel.inspectItem(name, desc)
                            },
                            currentCustomer = currentCustomer,
                            onTapCustomer = {
                                viewModel.serveCurrentCustomer()
                            },
                            cauldronColorHex = cauldronColorHex,
                            onTapCauldron = {
                                viewModel.openBrewingStation()
                            },
                            onTapCrystalBall = {
                                viewModel.openCrystalBallDialog()
                            }
                        )

                        if (recentSale != null) {
                            SaleCelebrationBadge(
                                message = recentSale!!,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 12.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.05f)
                            .fillMaxHeight()
                            .background(StarlightTwilight, RoundedCornerShape(18.dp))
                            .border(1.dp, PastelLavender.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            TopActionHeader(
                                title = "Starlight Apothecary",
                                subtitle = when (dayPhase) {
                                    DayPhase.MORNING_PREP -> "Morning Prep • Stock Up!"
                                    DayPhase.SHOP_OPEN -> "Patron $waveIndex/${dailyForecast.expectedCustomers}"
                                    DayPhase.DAY_SUMMARY -> "Evening Ledger"
                                },
                                unclaimedQuestsCount = unclaimedQuestsCount,
                                onOpenCrystalBall = { viewModel.openCrystalBallDialog() },
                                onOpenQuests = { viewModel.openQuestsDialog() },
                                onOpenMenu = { viewModel.openMenu() }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            HudStatsRow(
                                day = day,
                                gold = gold,
                                inventoryCount = inventoryCount,
                                totalIngredientsCount = totalIngredientsCount
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            PotionStockStrip(potionStock = potionStock)

                            Spacer(modifier = Modifier.height(8.dp))

                            ActiveMiddleSection(
                                dayPhase = dayPhase,
                                dailyForecast = dailyForecast,
                                currentCustomer = currentCustomer,
                                potionStock = potionStock,
                                ingredientPantry = ingredientPantry,
                                gold = gold,
                                inspectedName = inspectedName,
                                inspectedDesc = inspectedDesc,
                                statusMessage = statusMessage,
                                onOpenCrystalBall = { viewModel.openCrystalBallDialog() },
                                onServe = { viewModel.serveCurrentCustomer() },
                                onQuickBrewAndServe = { potion ->
                                    viewModel.quickBrewPotion(potion)
                                },
                                onPixieExpress = { viewModel.pixieExpressServeCurrentCustomer() },
                                onDismissInspection = { viewModel.clearInspection() }
                            )
                        }

                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            BottomActionControls(
                                dayPhase = dayPhase,
                                foragedToday = foragedToday,
                                isShopOpen = isShopOpen,
                                onOpenMarket = { viewModel.openMarketDialog() },
                                onOpenBrewing = { viewModel.openBrewingStation() },
                                onToggleShop = { viewModel.toggleShopOpen() }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            ShopStatusPill(dayPhase = dayPhase)
                        }
                    }
                }
            } else {
                // Portrait Layout: Vertical stack prioritizing the isometric cottage canvas
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    TopActionHeader(
                        title = "Fantasy Apothecary Tycoon",
                        subtitle = when (dayPhase) {
                            DayPhase.MORNING_PREP -> "Morning Prep • Buy, Forage & Brew"
                            DayPhase.SHOP_OPEN -> "Open • Patron $waveIndex of ${dailyForecast.expectedCustomers}"
                            DayPhase.DAY_SUMMARY -> "Day $day Complete"
                        },
                        unclaimedQuestsCount = unclaimedQuestsCount,
                        onOpenCrystalBall = { viewModel.openCrystalBallDialog() },
                        onOpenQuests = { viewModel.openQuestsDialog() },
                        onOpenMenu = { viewModel.openMenu() }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    HudStatsRow(
                        day = day,
                        gold = gold,
                        inventoryCount = inventoryCount,
                        totalIngredientsCount = totalIngredientsCount
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    PotionStockStrip(potionStock = potionStock)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        ApothecaryShopCanvas(
                            onInspectObject = { name, desc ->
                                viewModel.inspectItem(name, desc)
                            },
                            currentCustomer = currentCustomer,
                            onTapCustomer = {
                                viewModel.serveCurrentCustomer()
                            },
                            cauldronColorHex = cauldronColorHex,
                            onTapCauldron = {
                                viewModel.openBrewingStation()
                            },
                            onTapCrystalBall = {
                                viewModel.openCrystalBallDialog()
                            }
                        )

                        if (recentSale != null) {
                            SaleCelebrationBadge(
                                message = recentSale!!,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)
                            )
                        }
                    }

                    ActiveMiddleSection(
                        dayPhase = dayPhase,
                        dailyForecast = dailyForecast,
                        currentCustomer = currentCustomer,
                        potionStock = potionStock,
                        ingredientPantry = ingredientPantry,
                        gold = gold,
                        inspectedName = inspectedName,
                        inspectedDesc = inspectedDesc,
                        statusMessage = statusMessage,
                        onOpenCrystalBall = { viewModel.openCrystalBallDialog() },
                        onServe = { viewModel.serveCurrentCustomer() },
                        onQuickBrewAndServe = { potion ->
                            viewModel.quickBrewPotion(potion)
                        },
                        onPixieExpress = { viewModel.pixieExpressServeCurrentCustomer() },
                        onDismissInspection = { viewModel.clearInspection() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BottomActionControls(
                        dayPhase = dayPhase,
                        foragedToday = foragedToday,
                        isShopOpen = isShopOpen,
                        onOpenMarket = { viewModel.openMarketDialog() },
                        onOpenBrewing = { viewModel.openBrewingStation() },
                        onToggleShop = { viewModel.toggleShopOpen() }
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    ShopStatusPill(dayPhase = dayPhase)
                }
            }

            // --- Modal Overlays ---
            if (isCrystalBallOpen) {
                CrystalBallDialog(
                    forecast = dailyForecast,
                    dayPhase = dayPhase,
                    potionStock = potionStock,
                    onOpenMarket = { viewModel.openMarketDialog() },
                    onDismiss = { viewModel.closeCrystalBallDialog() }
                )
            }

            if (isMarketOpen) {
                MorningMarketDialog(
                    gold = gold,
                    forecast = dailyForecast,
                    ingredientPantry = ingredientPantry,
                    foragedToday = foragedToday,
                    isMorningPrep = dayPhase == DayPhase.MORNING_PREP,
                    onForageGarden = { viewModel.forageGarden() },
                    onBuyIngredient = { ing, qty -> viewModel.buyIngredient(ing, qty) },
                    onBuyTrioBundle = { viewModel.buyStarterBundle() },
                    onDismiss = { viewModel.closeMarketDialog() }
                )
            }

            if (isQuestsOpen) {
                QuestsDialog(
                    quests = quests,
                    onClaimReward = { viewModel.claimQuestReward(it) },
                    onDismiss = { viewModel.closeQuestsDialog() }
                )
            }

            if (isBrewingOpen) {
                BrewingDialog(
                    selectedPotion = selectedPotion,
                    brewingStep = brewingStep,
                    stirCount = stirCount,
                    inventoryCount = inventoryCount,
                    potionStock = potionStock,
                    ingredientPantry = ingredientPantry,
                    onSelectPotion = { viewModel.selectBrewPotion(it) },
                    onAddIngredients = { viewModel.addIngredients() },
                    onStir = { viewModel.stirCauldron() },
                    onBottle = { viewModel.bottlePotion() },
                    onQuickBrew = { viewModel.quickBrewPotion(it) },
                    onOpenMarket = { viewModel.openMarketDialog() },
                    onDismiss = { viewModel.closeBrewingStation() }
                )
            }

            if (isMenuOpen) {
                ApothecaryMenuDialog(
                    day = day,
                    gold = gold,
                    inventoryCount = inventoryCount,
                    potionStock = potionStock,
                    ingredientPantry = ingredientPantry,
                    dayPhase = dayPhase,
                    totalCustomersServed = totalServed,
                    onDismiss = { viewModel.closeMenu() }
                )
            }

            if (dayPhase == DayPhase.DAY_SUMMARY) {
                EndOfDayDialog(
                    summary = dailySummary,
                    totalGold = gold,
                    onStartNextDay = { viewModel.startNextDay() }
                )
            }
        }
    }
}

@Composable
private fun TopActionHeader(
    title: String,
    subtitle: String,
    unclaimedQuestsCount: Int,
    onOpenCrystalBall: () -> Unit,
    onOpenQuests: () -> Unit,
    onOpenMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ButtercreamGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = PastelLavender,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onOpenCrystalBall,
                modifier = Modifier.testTag("crystal_ball_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Crystal Ball Forecast",
                    tint = PastelLavender
                )
            }
            QuestsIconButton(
                unclaimedCount = unclaimedQuestsCount,
                onClick = onOpenQuests
            )
            IconButton(
                onClick = onOpenMenu,
                modifier = Modifier.testTag("menu_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Menu",
                    tint = SoftCream
                )
            }
        }
    }
}

@Composable
private fun HudStatsRow(
    day: Int,
    gold: Int,
    inventoryCount: Int,
    totalIngredientsCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        StatBadge(
            icon = Icons.Default.Today,
            label = "Day",
            value = "$day",
            iconTint = PastelLavender,
            modifier = Modifier.weight(0.8f)
        )
        StatBadge(
            icon = Icons.Default.MonetizationOn,
            label = "Gold",
            value = "$gold G",
            iconTint = ButtercreamGold,
            modifier = Modifier.weight(1.1f)
        )
        StatBadge(
            icon = Icons.Default.Science,
            label = "Shelf",
            value = "$inventoryCount/${GameViewModel.MAX_SHELF_CAPACITY}",
            iconTint = PastelSky,
            modifier = Modifier.weight(1.05f)
        )
        StatBadge(
            icon = Icons.Default.Eco,
            label = "Herbs",
            value = "$totalIngredientsCount",
            iconTint = PastelMint,
            modifier = Modifier.weight(0.85f)
        )
    }
}

@Composable
private fun PotionStockStrip(potionStock: Map<PotionType, Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PotionType.values().forEach { potion ->
            val count = potionStock[potion] ?: 0
            Surface(
                color = StarlightPlum.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (count == 0) PastelRose.copy(alpha = 0.65f) else potion.color.copy(alpha = 0.45f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(potion.color, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = potion.shortName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SoftCream
                        )
                    }
                    Text(
                        text = "$count",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (count == 0) PastelRose else ButtercreamGold
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveMiddleSection(
    dayPhase: DayPhase,
    dailyForecast: DailyForecast,
    currentCustomer: Customer?,
    potionStock: Map<PotionType, Int>,
    ingredientPantry: Map<IngredientType, Int>,
    gold: Int,
    inspectedName: String?,
    inspectedDesc: String?,
    statusMessage: String,
    onOpenCrystalBall: () -> Unit,
    onServe: () -> Unit,
    onQuickBrewAndServe: (PotionType) -> Unit,
    onPixieExpress: () -> Unit,
    onDismissInspection: () -> Unit
) {
    when {
        currentCustomer != null && currentCustomer.phase == CustomerPhase.WAITING_FOR_ORDER -> {
            val requested = currentCustomer.requestedPotion
            val matchingStock = potionStock[requested] ?: 0
            val reqIng = requested.requiredIngredient
            val matchingHerb = ingredientPantry[reqIng] ?: 0
            val marketPrice = dailyForecast.ingredientPrices[reqIng] ?: reqIng.basePrice
            val pixieFee = marketPrice * 2

            CustomerOrderCard(
                customer = currentCustomer,
                matchingStock = matchingStock,
                matchingHerb = matchingHerb,
                pixieFee = pixieFee,
                canAffordPixie = gold >= pixieFee,
                onServe = onServe,
                onQuickBrew = { onQuickBrewAndServe(requested) },
                onPixieExpress = onPixieExpress
            )
        }

        inspectedName != null && inspectedDesc != null -> {
            InspectionCard(
                title = inspectedName,
                description = inspectedDesc,
                onDismiss = onDismissInspection
            )
        }

        dayPhase == DayPhase.MORNING_PREP -> {
            ForecastBannerCard(
                forecast = dailyForecast,
                onClick = onOpenCrystalBall
            )
        }

        else -> {
            StatusMessageCard(message = statusMessage)
        }
    }
}

@Composable
private fun ForecastBannerCard(
    forecast: DailyForecast,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StarlightPlum),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PastelLavender.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("forecast_banner_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = LightAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Forecast: ${forecast.eventTitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ButtercreamGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${forecast.expectedCustomers} patrons • Health ${forecast.healthDemandPct}% | Mana ${forecast.manaDemandPct}% | Stam ${forecast.staminaDemandPct}%",
                        fontSize = 11.sp,
                        color = OnSurfaceMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = "Orb",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PastelLavender
            )
        }
    }
}

@Composable
private fun BottomActionControls(
    dayPhase: DayPhase,
    foragedToday: Boolean,
    isShopOpen: Boolean,
    onOpenMarket: () -> Unit,
    onOpenBrewing: () -> Unit,
    onToggleShop: () -> Unit
) {
    if (dayPhase == DayPhase.MORNING_PREP) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onOpenMarket,
                modifier = Modifier
                    .weight(1.1f)
                    .height(48.dp)
                    .testTag("open_market_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                BadgedBox(
                    badge = {
                        if (!foragedToday) {
                            Badge(containerColor = PastelMint)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = null,
                        tint = PastelMint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Market",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SoftCream
                )
            }

            Button(
                onClick = onOpenBrewing,
                modifier = Modifier
                    .weight(0.95f)
                    .height(48.dp)
                    .testTag("open_brewing_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StarlightMist,
                    contentColor = SoftCream
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = LightAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Brew",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OpenShopActionButton(
                isShopOpen = isShopOpen,
                onClick = onToggleShop,
                modifier = Modifier.weight(1.15f)
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onOpenBrewing,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("open_brewing_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StarlightMist,
                    contentColor = SoftCream
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = LightAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Cauldron",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            OpenShopActionButton(
                isShopOpen = isShopOpen,
                onClick = onToggleShop,
                modifier = Modifier.weight(1.25f)
            )
        }
    }
}

@Composable
fun QuestsIconButton(
    unclaimedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.testTag("quests_button")
    ) {
        BadgedBox(
            badge = {
                if (unclaimedCount > 0) {
                    Badge(
                        containerColor = ButtercreamGold,
                        contentColor = Color(0xFF231604)
                    ) {
                        Text(
                            text = "$unclaimedCount",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Daily Quests",
                tint = if (unclaimedCount > 0) LightAmber else OnSurfaceMuted
            )
        }
    }
}

@Composable
fun CustomerOrderCard(
    customer: Customer,
    matchingStock: Int,
    matchingHerb: Int,
    pixieFee: Int,
    canAffordPixie: Boolean,
    onServe: () -> Unit,
    onQuickBrew: () -> Unit,
    onPixieExpress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val patienceColor = when {
        customer.patienceRemaining > 0.55f -> PastelMint
        customer.patienceRemaining > 0.25f -> ButtercreamGold
        else -> PastelRose
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = StarlightMist),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(customer.requestedPotion.colorHex)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("customer_order_card")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(customer.type.cloakColorHex), CircleShape)
                            .border(1.5.dp, ButtercreamGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = customer.type.displayName,
                            tint = SoftCream,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = customer.type.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SoftCream
                        )
                        Text(
                            text = "Wants: ${customer.requestedPotion.displayName} (Shelf: $matchingStock)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(customer.requestedPotion.colorHex)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                when {
                    matchingStock > 0 -> {
                        Button(
                            onClick = onServe,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("serve_customer_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ButtercreamGold,
                                contentColor = Color(0xFF231604)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Sell +${customer.requestedPotion.price}G",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    matchingHerb > 0 -> {
                        Button(
                            onClick = onQuickBrew,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("quick_brew_customer_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelMint,
                                contentColor = Color(0xFF052E16)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Quick Brew",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    else -> {
                        Button(
                            onClick = onPixieExpress,
                            enabled = canAffordPixie,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("pixie_express_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PastelRose,
                                contentColor = Color(0xFF3B0712),
                                disabledContainerColor = Color(0xFF4B5563),
                                disabledContentColor = Color(0xFF9CA3AF)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pixie (-${pixieFee}G)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { customer.patienceRemaining.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = patienceColor,
                trackColor = Color(0x33FFFFFF)
            )
        }
    }
}

@Composable
fun SaleCelebrationBadge(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF164336),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelMint),
        shadowElevation = 8.dp,
        modifier = modifier.testTag("sale_celebration_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = ButtercreamGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = message,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PastelMint
            )
        }
    }
}

@Composable
fun StatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = StarlightPlum,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x28FFFFFF)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = OnSurfaceMuted,
                    maxLines = 1
                )
                Text(
                    text = value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = SoftCream,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ShopStatusPill(
    dayPhase: DayPhase,
    modifier: Modifier = Modifier
) {
    val (pillBg, pillBorder, pillTextColor, dotColor, label) = when (dayPhase) {
        DayPhase.MORNING_PREP -> Tuple5(
            StarlightPlum,
            ButtercreamGold.copy(alpha = 0.7f),
            LightAmber,
            ButtercreamGold,
            "MORNING PREP • MARKET & CAULDRON READY"
        )
        DayPhase.SHOP_OPEN -> Tuple5(
            Color(0xFF153E32),
            PastelMint,
            PastelMint,
            PastelMint,
            "COTTAGE SHOP OPEN • SERVE ADVENTURERS"
        )
        DayPhase.DAY_SUMMARY -> Tuple5(
            StarlightPlum,
            PastelLavender,
            PastelLavender,
            PastelLavender,
            "EVENING STARLIGHT • DAY SUMMARY"
        )
    }

    Surface(
        color = pillBg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, pillBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("shop_status_pill")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = pillTextColor,
                letterSpacing = 0.4.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A,
    val b: B,
    val c: C,
    val d: D,
    val e: E
)

@Composable
fun OpenShopActionButton(
    isShopOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("open_shop_button"),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isShopOpen) PastelRose else ButtercreamGold,
            contentColor = Color(0xFF231604)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isShopOpen) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isShopOpen) "End Day" else "Open Shop",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun StatusMessageCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StarlightPlum.copy(alpha = 0.88f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x28FFFFFF)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("status_message_card")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = ButtercreamGold,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = SoftCream,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun InspectionCard(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StarlightMist),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, PastelLavender),
        modifier = modifier
            .fillMaxWidth()
            .testTag("inspection_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ButtercreamGold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = SoftCream,
                    lineHeight = 16.sp
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("dismiss_inspection_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss inspection",
                    tint = SoftCream,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

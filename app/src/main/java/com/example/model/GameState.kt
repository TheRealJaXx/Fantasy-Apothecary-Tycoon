package com.example.model

data class GameState(
    val gold: Int = 150,
    val day: Int = 1,
    val inventoryCount: Int = 6,
    val maxInventoryCapacity: Int = 20,
    val isShopOpen: Boolean = false,
    val isMenuOpen: Boolean = false,
    val lastStatusMessage: String = "Welcome to your apothecary! Tap 'Open Shop' to begin your trade.",
    val inspectedItemName: String? = null,
    val inspectedItemDescription: String? = null
)

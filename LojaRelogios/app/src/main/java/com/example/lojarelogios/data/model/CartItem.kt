package com.example.lojarelogios.data.model

data class CartItem(
    val cartId: String = "",
    val itemId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = ""
) {
    constructor() : this(cartId = "", itemId = "", name = "", price = 0.0, quantity = 0, imageUrl = "")
}

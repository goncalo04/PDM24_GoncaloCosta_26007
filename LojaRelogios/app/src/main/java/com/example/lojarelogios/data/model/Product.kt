package com.example.lojarelogios.data.model

data class Product(
    val id: Int = 0,
    val image: String = "",
    val name: String = "",
    val price: Double = 0.0,

) {
    // Construtor sem argumentos necessário para o Firestore
    constructor() : this(id = 0, image = "", name = "", price = 0.0)
}

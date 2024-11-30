package com.example.pdmnoticias.data.model

data class NewsResponse(
    val status: String,
    val numResults: Int,
    val results: List<NewsItem>
)

package com.example.pdmnoticias.data.model

data class NewsResponse(
    val status: String,
    val results: List<NewsItem>
)

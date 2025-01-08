package com.example.pdmnoticias.domain.repository

import com.example.pdmnoticias.domain.model.NewsItem

interface NewsRepository {
    suspend fun getTopStories(): List<NewsItem>
}

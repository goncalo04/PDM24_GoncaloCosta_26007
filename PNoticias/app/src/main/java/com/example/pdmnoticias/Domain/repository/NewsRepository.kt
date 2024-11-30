package com.example.pdmnoticias.Domain.repository

import androidx.compose.ui.input.key.Key
import com.example.pdmnoticias.data.model.NewsItem

interface NewsRepository{

    suspend fun getTopStories (apiKey: String): List<NewsItem>


}
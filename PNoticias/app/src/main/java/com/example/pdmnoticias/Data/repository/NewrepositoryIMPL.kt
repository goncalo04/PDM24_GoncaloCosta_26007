package com.example.pdmnoticias.data.repository

import com.example.pdmnoticias.domain.repository.NewsRepository
import com.example.pdmnoticias.data.api.NewsApiService
import com.example.pdmnoticias.domain.model.NewsItem

class NewsRepositoryImpl(private val apiService: NewsApiService) : NewsRepository {

    override suspend fun getTopStories(): List<NewsItem> {
        val response = apiService.getTopStories()
        return response.results.map { it.toDomainModel() }
    }
}
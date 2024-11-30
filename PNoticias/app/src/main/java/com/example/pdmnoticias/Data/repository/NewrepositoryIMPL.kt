package com.example.pdmnoticias.data.repository

import com.example.pdmnoticias.Domain.repository.NewsRepository
import com.example.pdmnoticias.data.api.NewsApiService
import com.example.pdmnoticias.data.api.RetrofitInstance
import com.example.pdmnoticias.data.model.NewsItem

class NewrepositoryIMPL(private val apiService: NewsApiService) : NewsRepository {

     override suspend fun getTopStories(apiKey: String): List<NewsItem> {
       return apiService.getTopStories(apiKey).results.map {
           it.Tonew()
        }

    }
}

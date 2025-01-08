package com.example.pdmnoticias.domain.usecase

import com.example.pdmnoticias.domain.model.NewsItem
import com.example.pdmnoticias.domain.repository.NewsRepository

class GetTopStoriesUseCase(private val newsRepository: NewsRepository) {
    suspend operator fun invoke(): List<NewsItem> {
        return newsRepository.getTopStories()
    }
}

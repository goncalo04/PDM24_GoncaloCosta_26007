package com.example.pdmnoticias.domain.usecase

import com.example.pdmnoticias.data.repository.NewrepositoryIMPL
import com.example.pdmnoticias.data.model.NewsItem


class GetTopStoriesUseCase(private val newrepositoryIMPL: NewrepositoryIMPL) {

    suspend operator fun invoke(apiKey: String): List<NewsItem> {
        return newrepositoryIMPL.getTopStories(apiKey)
    }
}

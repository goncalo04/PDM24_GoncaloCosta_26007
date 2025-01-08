package com.example.pdmnoticias.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdmnoticias.domain.model.NewsItem
import com.example.pdmnoticias.domain.usecase.GetTopStoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val getTopStoriesUseCase: GetTopStoriesUseCase) : ViewModel() {

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> get() = _news

    fun getTopStories() {
        viewModelScope.launch {
            try {
                _news.value = getTopStoriesUseCase()
            } catch (e: Exception) {
                _news.value = emptyList()
            }
        }
    }
}

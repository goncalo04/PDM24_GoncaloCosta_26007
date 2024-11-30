package com.example.pdmnoticias.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pdmnoticias.data.api.RetrofitInstance
import com.example.pdmnoticias.data.repository.NewrepositoryIMPL
import com.example.pdmnoticias.data.model.NewsItem
import com.example.pdmnoticias.domain.usecase.GetTopStoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val newrepositoryIMPL: NewrepositoryIMPL) : ViewModel() {

    private val api= RetrofitInstance.api
    private val repository =  NewrepositoryIMPL(api)
    private val getTopStoriesUseCase = GetTopStoriesUseCase(repository)

    val news = MutableStateFlow<List<NewsItem>>(emptyList())

    fun GetTopStories ()
    {
        viewModelScope.launch {
            try {
            news.value= getTopStoriesUseCase(apiKey = "S82U2vRzD7nZ3CdxE9D8APIsbytbWlDr")
        }
            catch (e:Exception){
                news.value= emptyList()
            }
        }
    }

}

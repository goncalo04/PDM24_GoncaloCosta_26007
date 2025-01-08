package com.example.pdmnoticias.data.api

import com.example.pdmnoticias.data.model.NewsResponse
import retrofit2.http.GET

interface NewsApiService {
    @GET("svc/topstories/v2/home.json?api-key=S82U2vRzD7nZ3CdxE9D8APIsbytbWlDr")
    suspend fun getTopStories(): NewsResponse
}

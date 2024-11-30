package com.example.pdmnoticias.data.api

import com.example.pdmnoticias.data.model.NewsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

val apiKey= "S82U2vRzD7nZ3CdxE9D8APIsbytbWlDr"

object RetrofitInstance{
    val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}

interface NewsApiService {

    @GET("topstories/v2/home.json")
    suspend fun getTopStories(
        @Query("api-key") apiKey: String
    ): NewsResponse
}
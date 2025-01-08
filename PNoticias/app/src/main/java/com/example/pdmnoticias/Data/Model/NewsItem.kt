package com.example.pdmnoticias.data.model

import com.example.pdmnoticias.domain.model.NewsItem

data class NewsItem(
    val title: String,
    val abstract: String,
    val url: String,
    var multimedia: List<Multimedia>?
) {
    fun toDomainModel(): NewsItem {
        val imageURL = multimedia?.getOrNull(1)?.url
            ?: "https://via.assets.so/img.jpg?w=400&h=150&tc=blue&bg=#cecece"

        return NewsItem(
            title = this.title,
            abstract = this.abstract,
            url = this.url,
            image_url = imageURL
        )
    }
}

data class Multimedia(
    val url: String
)

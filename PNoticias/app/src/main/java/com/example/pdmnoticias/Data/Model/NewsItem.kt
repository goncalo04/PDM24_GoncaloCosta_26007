package com.example.pdmnoticias.data.model

data class NewsItem(
    val title: String,
    val abstract: String,
    val url: String

)

{
    fun Tonew (): NewsItem
    { return NewsItem(
        title = title,
        abstract = abstract,
        url=url
    )

    }
}
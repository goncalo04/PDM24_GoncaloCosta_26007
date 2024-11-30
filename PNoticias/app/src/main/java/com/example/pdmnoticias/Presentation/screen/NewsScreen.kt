package com.example.pdmnoticias.presentation.screen



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.pdmnoticias.presentation.viewmodel.NewsViewModel


@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    LaunchedEffect(Unit) {viewModel.GetTopStories()
    }

    val news by viewModel.news.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(news) { item ->
            Text(item.url)
        }


    }

}

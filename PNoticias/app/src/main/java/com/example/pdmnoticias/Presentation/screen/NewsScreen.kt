package com.example.pdmnoticias.presentation.screen

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pdmnoticias.presentation.viewmodel.NewsViewModel

@Composable
fun NewsScreen(viewModel: NewsViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.getTopStories()
    }

    val news by viewModel.news.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        items(news) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("details/${Uri.encode(item.title)}/${Uri.encode(item.abstract)}/${Uri.encode(item.url)}")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.image_url,
                    contentDescription = "Notícia",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

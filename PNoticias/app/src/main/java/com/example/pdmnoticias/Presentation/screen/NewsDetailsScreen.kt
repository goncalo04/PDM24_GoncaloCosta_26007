package com.example.pdmnoticias.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@Composable
fun NewsDetailsScreen(title: String, abstract: String, url: String) {
    // Obter o contexto dentro da função @Composable
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Título da notícia
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Descrição da notícia
        Text(
            text = abstract,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Botão para abrir a URL
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
        ) {
            Text(text = "Leia Mais")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsDetailsScreenPreview() {
    NewsDetailsScreen(
        title = "Exemplo de Título",
        abstract = "Este é um exemplo de resumo da notícia.",
        url = "https://www.example.com"
    )
}

package com.example.lojarelogios.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberImagePainter
import com.example.lojarelogios.R
import com.example.lojarelogios.data.firebase.FirebaseUtils
import com.example.lojarelogios.data.model.Product
import com.example.lojarelogios.data.model.CartItem

@Composable
fun HomeScreen(
    onCategoryClick: (String) -> Unit,
    onSessionClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val context = LocalContext.current

    // Função para buscar todos os produtos do Firestore
    LaunchedEffect(true) {
        FirebaseUtils.getProducts { fetchedProducts ->
            products = fetchedProducts
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Cabeçalho fixo
        Header(
            onSessionClick = onSessionClick,
            onCartClick = onCartClick
        )

        // Exibe todos os produtos sem filtro de categoria
        ProductGrid(products = products, onCartClick = onCartClick)
    }
}

@Composable
fun Header(onSessionClick: () -> Unit, onCartClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Relogios.pt",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )

        // Ícone de Cesto
        IconButton(onClick = onCartClick) {
            Icon(
                painter = painterResource(id = R.drawable.cart),
                contentDescription = "Cesto",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Ícone de Sessão
        IconButton(onClick = onSessionClick) {
            Icon(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Sessão",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProductGrid(products: List<Product>, onCartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Permite o scroll
    ) {
        Text(
            text = "Produtos",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Grid de produtos
        products.chunked(2).forEach { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowProducts.forEach { product ->
                    ProductCard(product = product, onCartClick = onCartClick)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onCartClick: () -> Unit) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) } // Estado para controlar a quantidade

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagem do relógio
            Image(
                painter = rememberImagePainter(product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp)
            )

            // Nome do relógio
            Text(
                text = product.name,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Preço do relógio
            Text(
                text = "€${product.price}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Seletor de quantidade
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Botão de menos
                IconButton(
                    onClick = { if (quantity > 1) quantity-- }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.minus), // Ícone de menos
                        contentDescription = "Diminuir quantidade",
                        modifier = Modifier.size(18.dp) // Tamanho do ícone de menos
                    )
                }

                // Texto da quantidade
                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Botão de mais
                IconButton(
                    onClick = { quantity++ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add), // Ícone de mais
                        contentDescription = "Aumentar quantidade",
                        modifier = Modifier.size(18.dp) // Tamanho do ícone de mais
                    )
                }
            }

            // Botão para adicionar ao carrinho
            Button(
                onClick = {
                    // Criar o item para adicionar ao Firebase com cartId
                    val novoproduto = CartItem(
                        cartId = "",  // Deixe vazio para que o método de adicionar gere um ID único
                        itemId = product.id.toString(),
                        name = product.name,
                        price = product.price,
                        quantity = quantity,
                        imageUrl = product.image.toString()
                    )

                    // Adicionar ao carrinho no Firebase
                    FirebaseUtils.addItemToCart(novoproduto, {
                        Toast.makeText(context, "Adicionado ao carrinho", Toast.LENGTH_SHORT).show()
                    }, {
                        Toast.makeText(context, "Falha ao adicionar: ${it.message}", Toast.LENGTH_SHORT).show()
                    })
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Adicionar")
            }

        }
    }
}

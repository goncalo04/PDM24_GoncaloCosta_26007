package com.example.lojarelogios.ui.screens.cart

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.lojarelogios.R
import com.example.lojarelogios.data.firebase.FirebaseUtils
import com.example.lojarelogios.data.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import android.content.ClipboardManager
import android.content.ClipData
import androidx.compose.foundation.verticalScroll

@Composable
fun CartScreen(onBackClick: () -> Unit, onCheckoutClick: () -> Unit) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var cartId by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Função para buscar os itens do carrinho
    LaunchedEffect(true) {
        FirebaseUtils.getCartItems(
            onSuccess = { fetchedItems ->
                cartItems = fetchedItems // Atualiza a lista com os itens do carrinho
            },
            onFailure = { exception ->
                Toast.makeText(context, "Erro ao carregar os itens: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Exibindo os itens do carrinho
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Carrinho",
            fontSize = 22.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (cartItems.isEmpty()) {
            Text("Seu carrinho está vazio.")
        } else {
            cartItems.forEach { item ->
                CartItemRow(item) {
                    FirebaseUtils.removeItemFromCart(item.itemId, {
                        FirebaseUtils.getCartItems(
                            onSuccess = { updatedItems ->
                                cartItems = updatedItems
                                Toast.makeText(context, "Item removido do carrinho", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { exception ->
                                Toast.makeText(context, "Erro ao carregar os itens: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }, { exception ->
                        Toast.makeText(context, "Erro ao remover item: ${exception.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Chama a função para limpar o carrinho
                    FirebaseUtils.clearCart(
                        onSuccess = {
                            // Limpa a lista de itens na UI
                            cartItems = emptyList()
                            // Exibe a mensagem de sucesso
                            Toast.makeText(context, "Compra feita com sucesso!", Toast.LENGTH_SHORT).show()
                            // Se você quiser redirecionar o usuário para outra tela, como a tela inicial:
                            onBackClick() // Retorna para a tela anterior (ou home, conforme sua navegação)
                        },
                        onFailure = { exception ->
                            // Caso ocorra algum erro ao limpar o carrinho
                            Toast.makeText(context, "Erro ao limpar o carrinho: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            ) {
                Text(text = "Pagar")
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    FirebaseUtils.shareCart(FirebaseAuth.getInstance().currentUser?.uid ?: "", { cartId ->
                        // Sucesso ao compartilhar o carrinho
                        Toast.makeText(context, "Carrinho compartilhado com ID: $cartId", Toast.LENGTH_SHORT).show()

                        // Copia o cartId para a área de transferência
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Cart ID", cartId)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "ID copiado para a área de transferência", Toast.LENGTH_SHORT).show()
                    }, { exception ->
                        // Falha ao compartilhar o carrinho
                        Toast.makeText(context, "Erro ao compartilhar carrinho: ${exception.message}", Toast.LENGTH_SHORT).show()
                    })
                }
            ) {
                Text("Compartilhar Carrinho")
            }
            Button(
                onClick = {
                    showDialog = true
                }
            ) {
                Text("Importar Carrinho")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onBackClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Voltar")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Importar Carrinho") },
            text = {
                Column {
                    Text("Insira o ID do carrinho para importar:")
                    TextField(
                        value = cartId,
                        onValueChange = { cartId = it },
                        label = { Text("ID do Carrinho") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cartId.isNotEmpty()) {
                            FirebaseUtils.importCart(cartId, {
                                // Após o sucesso, chama a navegação para a tela de pagamento
                                onCheckoutClick() // Navega para a tela de pagamento
                                Toast.makeText(context, "Carrinho importado com sucesso!", Toast.LENGTH_SHORT).show()
                            }, { exception ->
                                // Erro no import
                                Toast.makeText(context, "Erro ao importar carrinho: ${exception.message}", Toast.LENGTH_SHORT).show()
                            })
                        } else {
                            Toast.makeText(context, "ID inválido", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Importar Carrinho")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun CartItemRow(cartItem: CartItem, onItemRemoved: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Imagem do produto à esquerda
        if (cartItem.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(cartItem.imageUrl),
                contentDescription = cartItem.name,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
            )
        }

        // Nome e detalhes do produto
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = cartItem.name, fontSize = 16.sp)
            Text(text = "€${cartItem.price}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Quantidade: ${cartItem.quantity}", fontSize = 14.sp, color = Color.Gray)
        }

        // Ícone de lixeira para remover item do carrinho
        IconButton(
            onClick = {
                onItemRemoved()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Remover do carrinho",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

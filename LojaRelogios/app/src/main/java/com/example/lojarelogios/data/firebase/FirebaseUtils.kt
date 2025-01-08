package com.example.lojarelogios.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.lojarelogios.data.model.CartItem
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.lojarelogios.data.model.Product
import com.google.firebase.firestore.FieldValue

object FirebaseUtils {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Função para buscar os itens do carrinho
    fun getCartItems(onSuccess: (List<CartItem>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("CartItems")
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener { snapshot ->
                    val cartItems = snapshot.documents.mapNotNull { document ->
                        CartItem(
                            itemId = document.getString("itemId") ?: "",
                            name = document.getString("name") ?: "",
                            price = document.getDouble("price") ?: 0.0,
                            quantity = document.getLong("quantity")?.toInt() ?: 0,
                            imageUrl = document.getString("imageUrl") ?: ""
                        )
                    }

                    println("Itens carregados: $cartItems") // Adicionei este println
                    onSuccess(cartItems)  // Passa os itens para a UI
                }
                .addOnFailureListener { onFailure(it) }
        }
    }


    // Função para remover um item do carrinho
    fun removeItemFromCart(itemId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val cartRef = firestore.collection("CartItems") // Coleção CartItems
                .document(userId) // Documento do usuário
                .collection("items") // Subcoleção 'items' onde os produtos estão armazenados

            // Encontrando o item pela propriedade itemId
            cartRef.whereEqualTo("itemId", itemId).get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.isEmpty) {
                        // Se o item for encontrado, deletamos o documento
                        snapshot.documents.forEach { document ->
                            document.reference.delete()
                        }
                        onSuccess() // Retorna sucesso após a remoção
                    } else {
                        // Caso o item não seja encontrado
                        onFailure(Exception("Item não encontrado no carrinho"))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }
    }


    fun clearCart(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("CartItems")
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener { snapshot ->
                    // Deletar todos os itens do carrinho
                    snapshot.documents.forEach { document ->
                        document.reference.delete()
                    }
                    onSuccess() // Chama a função de sucesso
                }
                .addOnFailureListener { exception ->
                    onFailure(exception) // Chama a função de falha
                }
        }
    }


    fun getProducts(onSuccess: (List<Product>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("products") // Certifique-se de que o nome da coleção esteja correto
            .get()
            .addOnSuccessListener { documents ->
                val productList = documents.map { document ->
                    Product(
                        image = document.getString("image") ?: "",
                        id = document.getLong("id")?.toInt() ?: 0,
                        name = document.getString("name") ?: "",
                        price = document.getDouble("price") ?: 0.0,


                        )
                }
                onSuccess(productList)
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error getting products: ", exception)
            }
    }

    fun shareCart(
        userId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val cartRef = firestore.collection("CartItems").document(userId).collection("items")

        // Gerando a chave de 4 dígitos para o ID do carrinho compartilhado
        val cartId = generateCartId(userId)

        // Referência para o carrinho compartilhado
        val sharedCartRef = firestore.collection("sharedCarts").document(cartId)

        // Obtendo os itens do carrinho do usuário
        cartRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onFailure(Exception("Carrinho vazio"))
                } else {
                    // Listando os itens do carrinho
                    val cartItems = snapshot.toObjects(CartItem::class.java)

                    // Salva os itens no "sharedCarts"
                    sharedCartRef.set(mapOf(
                        "cartId" to cartId,
                        "items" to cartItems,
                        "sharedWith" to listOf(userId)  // Adiciona o userId no campo sharedWith
                    ))
                        .addOnSuccessListener {
                            onSuccess(cartId)
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }



    fun generateCartId(userId: String): String {
        // Pega os primeiros 4 caracteres do userId e adiciona um número aleatório
        return "${userId.take(4)}"
    }

    fun importCart(
        cartId: String,
        onSuccess: (List<CartItem>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Referência aos itens do carrinho compartilhado usando o cartId
        val cartRef = firestore.collection("sharedCarts").document(cartId).collection("items")

        cartRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // Caso o carrinho compartilhado não seja encontrado
                    onFailure(Exception("Carrinho compartilhado não encontrado"))
                } else {
                    // Obtendo os itens do carrinho compartilhado
                    val importedItems = snapshot.toObjects(CartItem::class.java)

                    // Agora, adicionamos esses itens ao carrinho do usuário atual
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    if (userId != null) {
                        val currentUserCartRef = firestore.collection("CartItems").document(userId).collection("items")
                        val batch = firestore.batch()

                        // Para cada item importado, criamos uma referência e o adicionamos no carrinho do usuário atual
                        importedItems.forEach { item ->
                            // Criar a referência do item com o cartId do carrinho compartilhado
                            val itemRef = currentUserCartRef.document(item.itemId)

                            // Adicionar os itens ao carrinho do utilizador atual
                            batch.set(itemRef, item.copy(cartId = cartId))  // Usamos o cartId do carrinho compartilhado
                        }

                        // Executa a operação de batch para adicionar os itens
                        batch.commit()
                            .addOnSuccessListener {
                                onSuccess(importedItems)  // Após sucesso, passamos os itens importados
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)  // Caso de erro ao adicionar os itens ao carrinho
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)  // Caso falhe ao recuperar os dados do carrinho compartilhado
            }
    }

    fun addItemToCart(cartItem: CartItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid  // Pega o userId do Firebase Auth

        if (userId != null) {
            val cartId = generateCartId(userId)  // Gera o cartId com o userId

            val cartRef = firestore.collection("CartItems")
                .document(userId)  // Documento do usuário
                .collection("items")  // Subcoleção dos itens do carrinho

            // Criação do item a ser adicionado ao carrinho
            cartRef.add(
                mapOf(
                    "cartId" to cartId,
                    "itemId" to cartItem.itemId,
                    "name" to cartItem.name,
                    "price" to cartItem.price,
                    "quantity" to cartItem.quantity,
                    "imageUrl" to cartItem.imageUrl
                )
            )
                .addOnSuccessListener {
                    onSuccess()  // Retorna sucesso
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)  // Retorna erro
                }
        } else {
            // Caso o userId seja nulo, retorna um erro
            onFailure(Exception("Usuário não autenticado"))
        }
    }
    fun addItemToSharedCart(cartItem: CartItem, userId: String) {
        // Verifica se já existe um sharedCart para o usuário
        val sharedCartRef = firestore.collection("sharedCarts")
            .document(userId)  // Documento do usuário, para associar o sharedCart com o userId

        // Se não existir, criamos um novo carrinho compartilhado
        sharedCartRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Se o carrinho compartilhado já existe, adicionamos o item
                    sharedCartRef.collection("items")
                        .document(cartItem.itemId)
                        .set(
                            mapOf(
                                "itemId" to cartItem.itemId,
                                "name" to cartItem.name,
                                "price" to cartItem.price,
                                "quantity" to cartItem.quantity,
                                "imageUrl" to cartItem.imageUrl
                            )
                        )
                } else {
                    // Caso contrário, criamos um novo sharedCart
                    sharedCartRef.set(
                        mapOf(
                            "cartId" to userId,  // Usando o userId como cartId
                            "sharedWith" to listOf(userId)  // Inicia com o usuário atual no "sharedWith"
                        )
                    )
                    sharedCartRef.collection("items")
                        .document(cartItem.itemId)
                        .set(
                            mapOf(
                                "itemId" to cartItem.itemId,
                                "name" to cartItem.name,
                                "price" to cartItem.price,
                                "quantity" to cartItem.quantity,
                                "imageUrl" to cartItem.imageUrl
                            )
                        )
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Erro ao adicionar ao sharedCart: ${exception.message}")
            }
    }
    }



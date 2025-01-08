package com.example.lojarelogios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.lojarelogios.data.firebase.FirebaseUtils
import com.example.lojarelogios.ui.screens.cart.CartScreen
import com.example.lojarelogios.ui.screens.home.HomeScreen
import com.example.lojarelogios.ui.screens.login.LoginScreen
import com.example.lojarelogios.ui.screens.login.RegisterScreen
import com.example.lojarelogios.ui.theme.LojaRelogiosTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LojaRelogiosTheme {
                // Estado para controlar a navegação entre as telas
                val currentScreen = remember { mutableStateOf("login") }
                val auth = FirebaseAuth.getInstance()

                // Navegação condicional para diferentes telas
                when (currentScreen.value) {
                    "login" -> LoginScreen(
                        onLoginSuccess = {
                            // Quando o login for bem-sucedido, navega para a tela principal
                            currentScreen.value = "home"
                        },
                        onRegisterClick = {
                            // Se o usuário clicar em "Criar Conta", navega para a tela de registro
                            currentScreen.value = "register"
                        }
                    )
                    "register" -> RegisterScreen(
                        onRegisterSuccess = {
                            // Depois do registro, volta para a tela de login
                            currentScreen.value = "login"
                        }
                    )
                    "home" -> HomeScreen(
                        onCategoryClick = { category ->
                            // Exibe a categoria selecionada
                            println("Categoria selecionada: $category")
                        },
                        onSessionClick = {
                            // Logout e volta para a tela de login
                            auth.signOut()
                            currentScreen.value = "login"
                        },
                        onCartClick = {

                            currentScreen.value = "cart"
                        }
                    )

                    "cart" -> CartScreen(
                        onBackClick = {
                            // Lógica para voltar para a tela anterior
                            currentScreen.value = "home" // Ou a tela que você desejar
                        },
                        onCheckoutClick = {
                            // Lógica para finalizar a compra
                            println("Finalizando a compra...") // Você pode adicionar a lógica de pagamento aqui
                        }
                    )

                }
            }
        }
    }
}

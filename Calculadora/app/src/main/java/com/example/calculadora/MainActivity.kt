package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadora.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(innerPadding: PaddingValues) {
    var displayText by remember { mutableStateOf("0") }
    var previousNumber by remember { mutableStateOf("") }
    var currentNumber by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Display(displayText)
        Spacer(modifier = Modifier.weight(1f))
        MyButton(
            onButtonClick = { value ->
                when {
                    value in listOf("+", "-", "*", "/") -> {
                        if (currentNumber.isNotEmpty()) {
                            previousNumber = currentNumber
                            currentNumber = ""
                            operation = value
                            displayText = operation
                        }
                    }
                    value == "=" -> {
                        if (previousNumber.isNotEmpty() && currentNumber.isNotEmpty() && operation.isNotEmpty()) {
                            displayText = calculate(previousNumber, currentNumber, operation)
                            previousNumber = ""
                            currentNumber = ""
                            operation = ""
                        }
                    }
                    else -> {
                        currentNumber += value
                        displayText = currentNumber
                    }
                }
            }
        )
    }
}

fun calculate(previous: String, current: String, operation: String): String {
    val num1 = previous.toDouble()
    val num2 = current.toDouble()
    return when (operation) {
        "+" -> (num1 + num2).toString()
        "-" -> (num1 - num2).toString()
        "*" -> (num1 * num2).toString()
        "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Error"
        else -> "Error"
    }
}

@Composable
fun Display(displayText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .padding(24.dp)
    ) {
        Text(
            text = displayText,
            fontSize = 48.sp,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MyButton(onButtonClick: (String) -> Unit) {
    val buttonModifier = Modifier
        .size(72.dp)
        .padding(4.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton("7", buttonModifier, onButtonClick)
            CalculatorButton("8", buttonModifier, onButtonClick)
            CalculatorButton("9", buttonModifier, onButtonClick)
            CalculatorButton("/", buttonModifier, onButtonClick, Color(0xFF00BCD4))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton("4", buttonModifier, onButtonClick)
            CalculatorButton("5", buttonModifier, onButtonClick)
            CalculatorButton("6", buttonModifier, onButtonClick)
            CalculatorButton("*", buttonModifier, onButtonClick, Color(0xFF00BCD4))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton("1", buttonModifier, onButtonClick)
            CalculatorButton("2", buttonModifier, onButtonClick)
            CalculatorButton("3", buttonModifier, onButtonClick)
            CalculatorButton("-", buttonModifier, onButtonClick, Color(0xFF00BCD4))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalculatorButton("0", buttonModifier, onButtonClick)
            CalculatorButton(".", buttonModifier, onButtonClick)
            CalculatorButton("=", buttonModifier, onButtonClick, Color(0xFFFF5722))
            CalculatorButton("+", buttonModifier, onButtonClick, Color(0xFF00BCD4))
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onButtonClick: (String) -> Unit,
    color: Color = Color(0xFF6200EE)
) {
    Button(
        onClick = { onButtonClick(label) },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(text = label, fontSize = 24.sp, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun MyButtonPreview() {
    CalculadoraTheme {
        CalculatorScreen(PaddingValues())
    }
}

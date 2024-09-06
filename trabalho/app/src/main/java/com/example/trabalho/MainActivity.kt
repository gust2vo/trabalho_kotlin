package com.example.trabalho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController, coroutineScope, drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Meu App") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                NavHost(navController = navController, startDestination = "formulario_produto") {
                    composable("formulario_produto") { FormularioProduto() }
                    composable("formulario_cliente") { FormularioCliente() }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Define uma cor sólida para o fundo da gaveta
    ) {
        Text(
            text = "Formulários",
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            text = "Formulário Produto",
            onClick = {
                navController.navigate("formulario_produto")
                coroutineScope.launch { drawerState.close() }
            }
        )
        DrawerMenuItem(
            text = "Formulário Cliente",
            onClick = {
                navController.navigate("formulario_cliente")
                coroutineScope.launch { drawerState.close() }
            }
        )
    }
}

@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() },
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun FormularioProduto() {
    var nomeProduto by remember { mutableStateOf("") }
    var preco by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    val mensagemErro = remember { mutableStateOf(false) }
    val mensagemSucesso = remember { mutableStateOf(false) }
    var checar by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
                .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .width(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cadastro de produto",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Digite o nome do produto:", modifier = Modifier.padding(8.dp))
        TextField(
            value = nomeProduto,
            onValueChange = { nomeProduto = it },
            modifier = Modifier.padding(8.dp)
        )

        Text(text = "Digite o preço do produto:", modifier = Modifier.padding(8.dp))
        TextField(
            value = preco,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() || it == '.' || it == ',' }) {
                    preco = newValue
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.padding(8.dp)
        )

        Text(text = "Digite a quantidade do produto:", modifier = Modifier.padding(8.dp))
        TextField(
            value = quantidade,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    quantidade = newValue
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.padding(8.dp)
        )

        Row {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Enviar notificação para clientes")
                    Checkbox(
                        checked = checar,
                        onCheckedChange = { checar = it }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Enviar notificação para colaboradores")
                    Checkbox(
                        checked = checar,
                        onCheckedChange = { checar = it }
                    )
                }
            }
        }

        Button(onClick = {
            mostrarMensagens(
                nomeProduto,
                preco.replace(',', '.').toFloatOrNull() ?: 0f,
                quantidade.toIntOrNull() ?: 0,
                mensagemErro,
                mensagemSucesso
            ) {
                nomeProduto = ""
                preco = ""
                quantidade = ""
            }
        }) {
            Text("Enviar")
        }

        ExibirAlertas(mensagemErro, mensagemSucesso)
    }
}

@Composable
fun FormularioCliente() {
    // Implementar o formulário de cliente aqui
    Text("Formulário Cliente")
}

fun validarCampos(
    nomeProduto: String,
    preco: Float,
    quantidade: Int,
    erro: MutableState<Boolean>,
    sucesso: MutableState<Boolean>
) {
    if (nomeProduto.isEmpty() || preco <= 0 || quantidade <= 0) {
        erro.value = true
        sucesso.value = false
    } else {
        erro.value = false
        sucesso.value = true
    }
}

fun mostrarMensagens(
    nomeProduto: String,
    preco: Float,
    quantidade: Int,
    erro: MutableState<Boolean>,
    sucesso: MutableState<Boolean>,
    onSucesso: () -> Unit
) {
    validarCampos(nomeProduto, preco, quantidade, erro, sucesso)
    if (sucesso.value) {
        onSucesso()
    }
}

@Composable
fun ExibirAlertas(mensagemErro: MutableState<Boolean>, mensagemSucesso: MutableState<Boolean>) {
    if (mensagemErro.value) {
        AlertDialog(
            onDismissRequest = { mensagemErro.value = false },
            title = { Text("Erro") },
            text = { Text("Por favor, preencha todos os campos corretamente") },
            confirmButton = {
                Button(onClick = { mensagemErro.value = false }) {
                    Text("Ok")
                }
            }
        )
    }

    if (mensagemSucesso.value) {
        AlertDialog(
            onDismissRequest = { mensagemSucesso.value = false },
            title = { Text("Sucesso") },
            text = { Text("Formulário enviado com sucesso") },
            confirmButton = {
                Button(onClick = { mensagemSucesso.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}

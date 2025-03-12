package org.example.project.ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.example.project.model.dto.ItemWithRelations
import org.example.project.service.ItemViewModel
import org.example.project.util.decodeBase64ToBitmap

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModelsScreen(navController: NavHostController, viewModel: ItemViewModel) {
    // Scrollbar status
    val scrollState = rememberScrollState()
    // Searchbar value
    var searchValue by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        println(uiState.response)
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Search bar
            SearchBar(
                query = searchValue, // Pasa directamente el estado actual
                onQueryChange = { newValue -> searchValue = newValue } // Actualiza el estado
            )
            // Model rows
            FlowRow(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
                overflow = FlowRowOverflow.Visible
            ) {
                if (uiState.success) {
                    uiState.items.forEach{ item ->
                        if (item.item.name?.contains(searchValue) == true || searchValue.length <= 2) {
                            ModelCard(item, navController)
                        }
                    }
                } else {
                    Text(uiState.response!!)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Buscar...",
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        }
    )
}

// Model individual cards
@Composable
fun ModelCard(item: ItemWithRelations, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(10.dp)
            .clickable(onClick = {
                // change screen and pass item id
                navController.navigate("model_details_screen/${item.item.itemId}")
            }),
        shape = RoundedCornerShape(30.dp),
        // Shadow for better visibility
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            // Image
            Image(
                painter = BitmapPainter(decodeBase64ToBitmap(item.images[0].base64Image!!)),
                contentDescription = item.item.description,
                contentScale = ContentScale.FillBounds, // Asegura que la imagen llena el área disponible
                modifier = Modifier.fillMaxSize()
            )

            // Caja de texto en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f)) // Fondo oscuro con transparencia
                    .align(Alignment.BottomCenter) // Alinea al fondo
                    .padding(8.dp), // Espaciado interno
                contentAlignment = Alignment.Center
            ) {
                    Text(
                        text = item.item.name!!,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.White, // Texto claro para contraste
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
            }
        }
    }
}


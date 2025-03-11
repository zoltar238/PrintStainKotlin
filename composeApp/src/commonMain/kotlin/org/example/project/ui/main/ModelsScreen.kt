package org.example.project.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.example.project.model.dto.ItemDto

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModelsScreen(navController: NavHostController, itemStatus: String, items: MutableList<ItemDto>) {
    // Scrollbar status
    val scrollState = rememberScrollState()
    // Searchbar value
    var searchValue by remember { mutableStateOf("") }

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
                if (itemStatus == "OK") {
                    items.forEach { item ->
                        if (item.name?.contains(searchValue) == true || searchValue.length <= 2) {
                            ModelCard(item, navController)
                        }
                    }
                } else {
                    Text(itemStatus)
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
fun ModelCard(item: ItemDto, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(10.dp)
            .clickable(onClick = {
                // change screen and pass item id
                navController.navigate("model_details_screen/${item.itemId}")
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
            // Imagen
//            Image(
//                painter = BitmapPainter(item.bitmapImages[0]),
//                contentDescription = item.description,
//                contentScale = ContentScale.FillBounds, // Asegura que la imagen llena el área disponible
//                modifier = Modifier.fillMaxSize()
//            )

            // Caja de texto en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f)) // Fondo oscuro con transparencia
                    .align(Alignment.BottomCenter) // Alinea al fondo
                    .padding(8.dp), // Espaciado interno
                contentAlignment = Alignment.Center
            ) {
                item.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.subtitle1,
                        color = Color.White, // Texto claro para contraste
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}


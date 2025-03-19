package org.example.project.ui.main.model

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.example.project.model.dto.ItemWithRelations
import org.example.project.ui.AppColors
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.SnackBarComponent
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.viewModel.ItemViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ModelsScreen(navController: NavHostController, itemViewModel: ItemViewModel) {
    // Scrollbar status
    val scrollState = rememberScrollState()
    // Searchbar value
    var searchValue by remember { mutableStateOf("") }
    // Database flow
    val itemUiState by itemViewModel.itemUiState.collectAsState()
    // Scaffold
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarColor = remember { mutableStateOf(AppColors.primaryColor) }

    // Change color based on state
    LaunchedEffect(itemUiState.success) {
        snackBarColor.value = if (itemUiState.success) AppColors.primaryColor else AppColors.errorColor
    }

    // Show snackbar when response changes
    LaunchedEffect(itemUiState.response) {
        if (!itemUiState.isLoading && itemUiState.response != null && itemUiState.response != "Item selected successfully") {
            snackbarHostState.showSnackbar(
                message = itemUiState.response!!,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Load items if the list of items is empty
    if (itemUiState.items.isEmpty()) {
        itemViewModel.getAllItems()
    }

    MaterialTheme {
        // Loading indicator
        if (itemUiState.isLoading) LoadingIndicator()

        // Snackbar
        SnackBarComponent(
            snackbarHostState = snackbarHostState,
            snackBarColor = snackBarColor
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = AppColors.primaryColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = {
                                navController.navigate("model_add_new")
                            }) {
                                Text(
                                    text = "+",
                                    fontSize = 30.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Search bar
                    SearchBar(
                        query = searchValue,
                        onQueryChange = { newValue -> searchValue = newValue }
                    )
                    // Model rows
                    FlowRow(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                            .fillMaxSize(),
                        overflow = FlowRowOverflow.Visible
                    ) {
                        if (itemUiState.success) {
                            itemUiState.items.forEach { item ->
                                if (item.item.name?.contains(searchValue) == true || searchValue.length <= 2) {
                                    ModelCard(item, navController, itemViewModel)
                                }
                            }
                        } else {
                            Text(itemUiState.response ?: "Unknown error")
                            // Snackbar
                        }
                    }
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
fun ModelCard(item: ItemWithRelations, navController: NavHostController, itemViewModel: ItemViewModel) {
    val uiState by itemViewModel.itemUiState.collectAsState()
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .padding(10.dp),
        shape = RoundedCornerShape(30.dp),
        // Shadow for better visibility
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.accentColor)
        ) {
            // Image
            Image(
                painter = BitmapPainter(decodeBase64ToBitmap(item.images[0].base64Image!!)),
                contentDescription = item.item.description,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = {
                        itemViewModel.getItemById(item.item.itemId)
                        // change screen
                        if (uiState.success and uiState.response.equals("Item selected successfully") && uiState.selectedItem != null) {
                            navController.navigate("model_details_screen")
                        }
                    }
                    )
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


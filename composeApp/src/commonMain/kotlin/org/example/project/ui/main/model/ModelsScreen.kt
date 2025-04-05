package org.example.project.ui.main.model

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import org.example.project.model.dto.ItemWithRelations
import org.example.project.ui.AppColors
import org.example.project.ui.component.AlertDialog
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.MessageToaster
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

    MaterialTheme {
        // Toaster
        MessageToaster(
            messageEvent = itemUiState.messageEvent,
            success = itemUiState.success,
            onMessageConsumed = { itemViewModel.consumeMessage() }
        )
        // Loading indicator
        if (itemUiState.isLoading) LoadingIndicator()

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
                                navController.navigate("model_add_new?option=new")
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
                        if (itemUiState.items.isNotEmpty()) {
                            itemUiState.items.forEach { item ->
                                if (item.item.name?.lowercase()
                                        ?.contains(searchValue.lowercase()) == true || searchValue.length <= 2
                                ) {
                                    ModelCard(item, navController, itemViewModel)
                                }
                            }
                        } else {
                            Text(itemUiState.messageEvent?.message ?: "No items found")
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
    val showContextMenu = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
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
                .background(AppColors.secondaryBackgroundColor)
        ) {
            // Options menu
            if (showContextMenu.value) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .zIndex(3f),
                    onClick = {
                        showContextMenu.value = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete image",
                        tint = AppColors.accentColor
                    )
                }
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = { showDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = AppColors.textOnPrimaryColor
                        )
                    ) {
                        AlertDialog(
                            show = showDialog,
                            onDismiss = { showDialog = false },
                            title = "Are you sure you want to delete this item?",
                            message = "Warning, this item will be deleted and you wont be able to recover it",
                            confirmButton = "Accept",
                            onConfirm = { itemViewModel.deleteItem(listOf(item)) },
                            dismissButton = "Cancel"
                        )
                        Text("Delete item")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = {
                            itemViewModel.getItemById(item.item.itemId)
                            navController.navigate("model_add_new?option=edit")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = AppColors.textOnPrimaryColor
                        )
                    ) {
                        Text("Modify item")
                    }
                }

            } else {
                Image(
                    painter = BitmapPainter(decodeBase64ToBitmap(item.images.first().base64Image!!)),
                    contentDescription = item.item.description,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = {
                            itemViewModel.getItemById(item.item.itemId)
                            // change screen
                            navController.navigate("model_details_screen")
                        })
                )

                // Menu icon
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .zIndex(3f),
                    onClick = {
                        showContextMenu.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Delete image",
                        tint = AppColors.accentColor
                    )
                }

                // Inferior text box
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
}



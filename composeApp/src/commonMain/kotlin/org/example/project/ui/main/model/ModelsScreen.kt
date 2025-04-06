package org.example.project.ui.main.model

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import org.example.project.model.dto.ItemWithRelations
import org.example.project.ui.AppColors
import org.example.project.ui.component.AlertDialog
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.MessageToaster
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.viewModel.ItemViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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

        Box(modifier = Modifier.fillMaxSize().background(AppColors.backgroundColor)) {
            Scaffold(
                containerColor = AppColors.backgroundColor,
                contentColor = AppColors.textOnBackgroundColor
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        // Search bar
                        SearchBar(
                            query = searchValue,
                            onQueryChange = { newValue -> searchValue = newValue }
                        )

                        // Status row
                        if (itemUiState.items.isNotEmpty()) {
                            Text(
                                text = "Showing ${
                                    itemUiState.items.count {
                                        it.item.name?.lowercase()
                                            ?.contains(searchValue.lowercase()) == true || searchValue.length <= 2
                                    }
                                } models",
                                style = MaterialTheme.typography.bodySmall,
                                color = AppColors.textOnBackgroundColor.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        // Model rows
                        FlowRow(
                            modifier = Modifier
                                .padding(16.dp)
                                .verticalScroll(scrollState)
                                .fillMaxSize(),
                            overflow = FlowRowOverflow.Visible,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                EmptyStateMessage(itemUiState.messageEvent?.message)
                            }
                        }
                    }

                    // Floating action button
                    FloatingActionButton(
                        onClick = { navController.navigate("model_add_new?option=new") },
                        containerColor = AppColors.primaryColor,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 24.dp, end = 24.dp)
                            .zIndex(10f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add model"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp).padding(bottom = 16.dp),
            tint = AppColors.primaryColor.copy(alpha = 0.5f)
        )
        Text(
            text = message ?: "No models found",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = AppColors.textOnBackgroundColor
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search models...",
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(placeholder, color = AppColors.textOnBackgroundColor.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AppColors.primaryColor
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = AppColors.primaryColor
                    )
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.primaryColor,
            unfocusedBorderColor = AppColors.primaryColor.copy(alpha = 0.5f),
            cursorColor = AppColors.primaryColor,
            focusedContainerColor = AppColors.backgroundColor,
            unfocusedContainerColor = AppColors.backgroundColor,
            focusedTextColor = AppColors.textOnBackgroundColor,
            unfocusedTextColor = AppColors.textOnBackgroundColor
        ),
        singleLine = true
    )
}

// Model individual cards
@Composable
fun ModelCard(item: ItemWithRelations, navController: NavHostController, itemViewModel: ItemViewModel) {
    val showContextMenu = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (showContextMenu.value) 0.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (showContextMenu.value) AppColors.secondaryBackgroundColor else Color.Transparent)
        ) {
            // Contenido normal (imagen de fondo, gradiente, etc.) - solo visible cuando el menú está oculto
            if (!showContextMenu.value) {
                // Background image
                Image(
                    painter = BitmapPainter(decodeBase64ToBitmap(item.images.first().base64Image!!)),
                    contentDescription = item.item.description,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = {
                            itemViewModel.getItemById(item.item.itemId)
                            navController.navigate("model_details_screen")
                        })
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 300f,
                                endY = 900f
                            )
                        )
                )

                // Bottom text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(color = Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = item.item.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Menu icon (more visible with a background)
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = { showContextMenu.value = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color.White
                        )
                    }
                }
            }

            // Menu de opciones - visible solo cuando showContextMenu es true
            androidx.compose.animation.AnimatedVisibility(
                visible = showContextMenu.value,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColors.secondaryBackgroundColor)
                ) {
                    // Close button
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .zIndex(3f),
                        onClick = { showContextMenu.value = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close menu",
                            tint = AppColors.accentColor
                        )
                    }

                    // Menu content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.item.name ?: "Model",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = AppColors.textOnPrimaryColor,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                itemViewModel.getItemById(item.item.itemId)
                                navController.navigate("model_add_new?option=edit")
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.primaryColor,
                                contentColor = AppColors.textOnPrimaryColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).padding(end = 4.dp)
                            )
                            Text("Edit")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            ),
                            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp).padding(end = 4.dp)
                            )
                            Text("Delete")
                        }
                    }
                }
            }

            // Dialog de confirmación para eliminar
            AlertDialog(
                show = showDialog,
                onDismiss = { showDialog = false },
                title = "Delete this model?",
                message = "This action cannot be undone and the model will be permanently deleted.",
                confirmButton = "Delete",
                onConfirm = { itemViewModel.deleteItem(listOf(item)) },
                dismissButton = "Cancel"
            )
        }
    }
}
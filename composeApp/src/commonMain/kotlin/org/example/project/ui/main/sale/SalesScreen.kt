package org.example.project.ui.main.sale

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import comexampleproject.Sale
import org.example.project.ui.AppColors
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.MessageToaster
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.SaleViewModel
import java.math.BigDecimal

@Composable
fun SalesScreen(saleViewModel: SaleViewModel, itemViewModel: ItemViewModel) {
    // Load sale data
    val saleUiState by saleViewModel.saleUiState.collectAsState()
    val itemUiState by itemViewModel.itemUiState.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(saleUiState.messageEvent?.message) {
        if (saleUiState.messageEvent?.message == "Sale deleted successfully") {
            // Todo make method so only the referenced item is update
            itemViewModel.updateItems()
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background color
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.secondaryBackgroundColor.copy(alpha = 0.3f))
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Header()

                Spacer(modifier = Modifier.height(24.dp))

                // Sales table header
                TableHeader()

                Spacer(modifier = Modifier.height(8.dp))

                // Sales list
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (saleUiState.sales.isNotEmpty() && itemUiState.items.isNotEmpty()) {
                        LazyColumn(
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(saleUiState.sales) { sale ->
                                val bitmap =
                                    itemUiState.items.find { it.item.itemId == sale.itemId }?.images?.firstOrNull()?.base64Image
                                val imageBitmap = bitmap?.let { decodeBase64ToBitmap(it) }

                                if (imageBitmap != null) {
                                    SaleItem(
                                        sale = sale,
                                        imageBitmap = imageBitmap,
                                        saleViewModel = saleViewModel
                                    )
                                }
                            }
                        }
                    } else {
                        EmptyState()
                    }
                }
            }

            // Toast and Loading indicator
            AnimatedVisibility(
                visible = saleUiState.isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                LoadingIndicator()
            }

            MessageToaster(
                messageEvent = saleUiState.messageEvent,
                success = saleUiState.success,
                onMessageConsumed = { saleViewModel.consumeMessage() }
            )
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Sales Overview",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Manage and track your sales records",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Button(
            onClick = { /* Add new sale functionality */ },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.accentColor
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Sale",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Sale")
        }
    }
}

@Composable
fun TableHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.accentColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.7f)
            )
            Text(
                text = "Sale ID",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = "Model",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = "Cost",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.7f)
            )
            Text(
                text = "Price",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.7f)
            )
            Text(
                text = "Date",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Status",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = "Actions",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}

@Composable
fun SaleItem(sale: Sale, imageBitmap: ImageBitmap, saleViewModel: SaleViewModel) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val statusColor = when (sale.status) {
        "CANCELED" -> AppColors.errorColor.copy(alpha = 0.2f)
        "COMPLETED" -> AppColors.successColor.copy(alpha = 0.2f)
        "IN_PROGRESS" -> AppColors.warningColor.copy(alpha = 0.2f)
        else -> AppColors.secondaryBackgroundColor
    }

    val statusTextColor = when (sale.status) {
        "CANCELED" -> AppColors.errorColor
        "COMPLETED" -> AppColors.successColor
        "IN_PROGRESS" -> AppColors.warningColor
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .weight(0.7f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Item Image",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            // Sale info
            Text(
                text = "#${sale.saleId}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = "${sale.itemId}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.8f)
            )
            Text(
                text = "$${sale.cost}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.7f)
            )
            Text(
                text = "$${sale.price}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.7f)
            )
            Text(
                text = "${sale.date?.toString()?.substringBefore("T") ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Status chip
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(statusColor)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = sale.status?.replace("_", " ") ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier.weight(0.6f),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Sale",
                        tint = AppColors.accentColor
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete Sale",
                        tint = AppColors.errorColor
                    )
                }
            }
        }
    }

    // Edit dialog
    if (showEditDialog) {
        EditSaleDialog(
            sale = sale,
            onDismiss = { showEditDialog = false },
            onSave = { showEditDialog = false },
            saleViewModel = saleViewModel
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Sale") },
            text = {
                Text("Are you sure you want to delete this sale? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        saleViewModel.deleteSale(sale.saleId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.errorColor
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No sales found",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your sales records will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EditSaleDialog(sale: Sale, saleViewModel: SaleViewModel, onDismiss: () -> Unit, onSave: (Sale) -> Unit) {
    var cost by remember { mutableStateOf(sale.cost) }
    var price by remember { mutableStateOf(sale.price) }
    var selectedStatus by remember { mutableStateOf(sale.status ?: "IN_PROGRESS") }
    var statusMenuExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("IN_PROGRESS", "COMPLETED", "CANCELED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Sale #${sale.saleId}") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = cost?.toString() ?: "",
                    onValueChange = { cost = it.toDoubleOrNull() ?: sale.cost },
                    label = { Text("Cost ($)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    }
                )

                OutlinedTextField(
                    value = price?.toString() ?: "",
                    onValueChange = { price = it.toDoubleOrNull() ?: sale.price },
                    label = { Text("Price ($)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                    }
                )

                // Status dropdown
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedStatus.replace("_", " "),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { statusMenuExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Status")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenu(
                            expanded = statusMenuExpanded,
                            onDismissRequest = { statusMenuExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.replace("_", " ")) },
                                    onClick = {
                                        selectedStatus = status
                                        statusMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        val color = when (status) {
                                            "CANCELED" -> AppColors.errorColor
                                            "COMPLETED" -> AppColors.successColor
                                            "IN_PROGRESS" -> AppColors.warningColor
                                            else -> Color.Gray
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(color, CircleShape)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Update the sale with new values
                    saleViewModel.modifySale(
                        saleId = sale.saleId,
                        cost = BigDecimal(cost ?: 0.0),
                        price = BigDecimal(price ?: 0.0),
                        status = selectedStatus
                    )
                    onSave(sale)
                },
                enabled = cost != sale.cost || price != sale.price || selectedStatus != sale.status,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}
package org.example.project.ui.main.sale

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.bar.*
import comexampleproject.Sale
import org.example.project.ui.AppColors
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.MessageToaster
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.SaleViewModel
import java.math.BigDecimal
import kotlin.random.Random

@Composable
fun SalesScreen(saleViewModel: SaleViewModel, itemViewModel: ItemViewModel) {
    // Load sale data
    val saleUiState by saleViewModel.saleUiState.collectAsState()
    val itemUiState by itemViewModel.itemUiState.collectAsState()

    LaunchedEffect(saleUiState.messageEvent?.message) {
        if (saleUiState.messageEvent?.message == "Sale deleted successfully") {
            // Todo make method so only the referenced item is update
            itemViewModel.updateItems()
        }
    }

    MaterialTheme {
        // Loading indicator
        if (saleUiState.isLoading) LoadingIndicator()

        // Toast
        MessageToaster(
            messageEvent = saleUiState.messageEvent,
            success = saleUiState.success,
            onMessageConsumed = { saleViewModel.consumeMessage() }
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sales Overview",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = "", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                Text(text = "Sale Id", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                Text(text = "Model", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                Text(text = "Cost", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                Text(text = "Price", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                Text(text = "Date", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            }
            if (saleUiState.sales.isNotEmpty() && itemUiState.items.isNotEmpty()) {
                LazyColumn {
                    items(saleUiState.sales) { sale ->
                        SaleItem(
                            sale,
                            decodeBase64ToBitmap(itemUiState.items.find { it.item.itemId == sale.itemId }?.images?.first()?.base64Image!!),
                            saleViewModel = saleViewModel
                        )
                    }
                }
            } else {
                Text(text = "No sales found", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SaleItem(sale: Sale, imageBitmap: ImageBitmap, saleViewModel: SaleViewModel) {
    val showContextMenu = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .weight(1f)
                .background(AppColors.secondaryBackgroundColor)
        ) {
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
                        contentDescription = "Close menu",
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
                        org.example.project.ui.component.AlertDialog(
                            show = showDialog,
                            onDismiss = { showDialog = false },
                            title = "Are you sure you want to delete this sale?",
                            message = "Warning, this sale will be deleted and you won't be able to recover it",
                            confirmButton = "Accept",
                            onConfirm = { saleViewModel.deleteSale(sale.saleId) },
                            dismissButton = "Cancel"
                        )
                        Text("Delete sale")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        onClick = { showEditDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = AppColors.textOnPrimaryColor
                        )
                    ) {
                        Text("Modify sale")
                    }
                }
            } else {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Item Image",
                    modifier = Modifier
                        .fillMaxWidth()
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
                        contentDescription = "Open menu",
                        tint = AppColors.accentColor
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Sale info
        Text(text = "${sale.saleId}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = "${sale.itemId}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = "${sale.cost}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = "${sale.price}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = "${sale.date}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    }

    if (showEditDialog) {
        EditSaleDialog(
            sale = sale,
            onDismiss = { showEditDialog = false },
            onSave = { showEditDialog = false },
            saleViewModel = saleViewModel
        )
    }
}

@Composable
fun EditSaleDialog(sale: Sale, saleViewModel: SaleViewModel, onDismiss: () -> Unit, onSave: (Sale) -> Unit) {
    var cost by remember { mutableStateOf(sale.cost) }
    var price by remember { mutableStateOf(sale.price) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Sale") },
        text = {
            Column {
                OutlinedTextField(
                    value = cost.toString(),
                    onValueChange = { cost = it.toDoubleOrNull() ?: sale.cost },
                    label = { Text("Cost") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price.toString(),
                    onValueChange = { price = it.toDoubleOrNull() ?: sale.price },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Update the sale with new values
                saleViewModel.modifySale(
                    saleId = sale.saleId,
                    cost = BigDecimal(cost!!),
                    price = BigDecimal(price!!),

                )
                onSave
            }, enabled = cost != sale.cost || price != sale.price) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DropdownMenu() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Selecciona una opción") }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        // Cuadro de selección
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text("Opción") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        )

        // Menú desplegable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Cierra el menú si se hace clic fuera
            properties = PopupProperties(focusable = true), // Habilita la interacción
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp) // Asegúrate de que no se solape
        ) {
            // Opción 1
            DropdownMenuItem(
                onClick = {
                    selectedOption = "Opción 1"
                    expanded = false
                },
                text = { Text("Opción 1") }  // Agregamos el texto correctamente aquí
            )

            // Opción 2
            DropdownMenuItem(
                onClick = {
                    selectedOption = "Opción 2"
                    expanded = false
                },
                text = { Text("Opción 2") }  // Agregamos el texto correctamente aquí
            )

            // Opción 3
            DropdownMenuItem(
                onClick = {
                    selectedOption = "Opción 3"
                    expanded = false
                },
                text = { Text("Opción 3") }  // Agregamos el texto correctamente aquí
            )
        }
    }
}

@Composable
fun LineChart(sales: List<Sale>) {
    val barChartData = BarChartData(
        categories = sales.groupBy { it.saleId }.map { (saleId, salesList) ->
            BarChartCategory(
                name = saleId.toString(),
                entries = salesList.map { sale ->
                    BarChartEntry(
                        //x = sale.date.toString().substring(0, sale.date.toString().lastIndexOf(" ")) + "\n",
                        // Todo: implement item names
                        x = "randomItemName",
                        y = sale.price?.toFloat() ?: 0f, // Valor del precio
                        color = Color(
                            Random.nextInt(256),
                            Random.nextInt(256),
                            Random.nextInt(256)
                        ), // Color dinámico
                        //data = sale // Incluimos el objeto completo como referencia
                    )
                }
            )
        }
    )
    BarChart(
        data = barChartData,
        config = BarChartConfig(
            thickness = 14.dp,
            cornerRadius = 7.dp,
        ),
        modifier = Modifier.height(500.dp),
        animation = ChartAnimation.Sequenced(),
    )
}

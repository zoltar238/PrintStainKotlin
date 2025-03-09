@file:Suppress("UNREACHABLE_CODE")

package org.example.project.ui.main

import org.example.project.model.dto.ItemDto
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.controller.ItemControllerFake
import org.example.project.model.dto.SaleDto
import org.example.project.model.entity.Item
import org.example.project.service.createNewSale
import java.sql.Timestamp
import java.time.Instant
import kotlin.reflect.full.memberProperties

@Composable
fun ModelDetailsScreen(navController: NavHostController, itemId: String?) {
    val longItemId = itemId?.toLong()
    val item = longItemId?.let { ItemControllerFake.getItemById(it) }
    // Scope
    val coroutineScope = rememberCoroutineScope()
    // Snack bar state
    val snackbarHostState = remember { SnackbarHostState() }
    // Initial snack-bar color
    val color = MaterialTheme.colors.primary
    val snackBarColor = remember { mutableStateOf(color) }

    MaterialTheme {
        // Scaffold to host snack bar
        Scaffold(snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    backgroundColor = snackBarColor.value,
                    contentColor = MaterialTheme.colors.onPrimary,
                    elevation = 5.dp
                )
            }
        }) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (item != null) {
                    // Pager status
//                    val pagerState = rememberPagerState(pageCount = { item.bitmapImages.size })
                    val pagerState = rememberPagerState(pageCount = { 12 })

                    // Horizontal pager of items
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                    ) { page ->
//                        Image(
//                            painter = BitmapPainter(item.bitmapImages[page]),
//                            contentDescription = item.description,
//                            contentScale = ContentScale.Fit,
//                            modifier = Modifier
//                                .width(200.dp)
//                                .padding(20.dp)
//                                .align(Alignment.CenterHorizontally),
//                        )
                    }

                    // Button container
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // "Previous" button
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Blue, shape = CircleShape)
                                .clickable {
                                    coroutineScope.launch {
                                        // Scroll to the previous item
                                        pagerState.scrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        //"Next" button
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color.Blue, shape = CircleShape)
                                .clickable {
                                    coroutineScope.launch {
                                        // Scroll to the next item
                                        pagerState.scrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        // Filtrar y agrupar las propiedades de dos en dos
                        val properties = Item::class.memberProperties
                            .filter { it.name != "base64Images" && it.name != "bitmapImages" }
                            .chunked(2) // Agrupar de 2 en 2

                        // Iterar sobre los pares
                        items(properties) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                pair.forEach { property ->
                                    TableCell(
                                        text = "${property.name}: ${property.get(item)}",
                                        weight = 1f
                                    )
                                }
                                if (pair.size < 2) {
                                    TableCell(
                                        text = "",
                                        weight = 1f
                                    )
                                }
                            }
                        }
                    }

                    // Sale view
                    ModelSale(itemId.toLong(), coroutineScope, snackBarColor, snackbarHostState)
                }

                Button(
                    onClick = { navController.navigate("main_app_view") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Go back")
                }
            }
        }

    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun ModelSale(
    itemId: Long,
    scope: CoroutineScope,
    color: MutableState<Color>,
    snackbarHostState: SnackbarHostState,
) {
    // Variables
    var cost by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    // SnackBarColors
    val primaryColor = MaterialTheme.colors.primary
    val errorColor = MaterialTheme.colors.error

    Column(
        Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = cost,
            onValueChange = { newText ->
                // Validar que el texto solo contenga números y un punto decimal
                if (newText.isEmpty() || newText.matches("^[0-9]*\\.?[0-9]*$".toRegex())) {
                    // Asegurarse de que tenga dos decimales
                    var formattedText = newText

                    // Si tiene un punto decimal, asegurarse de que haya dos decimales
                    if (formattedText.contains(".")) {
                        val parts = formattedText.split(".")
                        if (parts.size == 2) {
                            // Limitar la parte decimal a dos decimales
                            val decimalPart = parts[1].take(2)
                            formattedText = "${parts[0]}.$decimalPart"
                        }
                    } else {
                        // Si no hay punto decimal, añadir .00
                        formattedText = "$formattedText.00"
                    }

                    // Actualizar el valor del texto con el formato
                    cost = formattedText
                }
            },
            label = { Text("Cost") },
            placeholder = { Text("0.00") },
            singleLine = true,
            // Show only number keyboard
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        // Price text field
        OutlinedTextField(
            value = price,
            onValueChange = { newText ->
                // Validar que el texto solo contenga números y un punto decimal
                if (newText.isEmpty() || newText.matches("^[0-9]*\\.?[0-9]*$".toRegex())) {
                    // Asegurarse de que tenga dos decimales
                    var formattedText = newText

                    // Si tiene un punto decimal, asegurarse de que haya dos decimales
                    if (formattedText.contains(".")) {
                        val parts = formattedText.split(".")
                        if (parts.size == 2) {
                            // Limitar la parte decimal a dos decimales
                            val decimalPart = parts[1].take(2)
                            formattedText = "${parts[0]}.$decimalPart"
                        }
                    } else {
                        // Si no hay punto decimal, añadir .00
                        formattedText = "$formattedText.00"
                    }

                    // Actualizar el valor del texto con el formato
                    price = formattedText
                }
            },
            label = { Text("Price") },
            placeholder = { Text("0.00") },
            singleLine = true,
            // Show only number keyboard
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )
    }

    Button(
        onClick = {
            scope.launch {
                val saleDto: SaleDto = SaleDto(
                    cost = cost.toBigDecimal(),
                    price = price.toBigDecimal(),
                    itemId = itemId,
                    date = Timestamp.from(Instant.now())
                )

                val serverResponse = createNewSale(saleDto)
                if (!serverResponse.success) {
                    color.value = errorColor
                } else {
                    color.value = primaryColor
                }
                snackbarHostState.showSnackbar(
                    message = serverResponse.data,
                    duration = SnackbarDuration.Short
                )
            }
        },
    ) {
        Text("Add sale")
    }
}

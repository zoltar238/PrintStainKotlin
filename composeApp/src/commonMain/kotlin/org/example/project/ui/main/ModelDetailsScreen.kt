package org.example.project.ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.model.dto.SaleDto
import org.example.project.ui.AppColors
import org.example.project.ui.component.BenefitSummary
import org.example.project.ui.component.CurrencyTextField
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.viewModel.ItemUiState
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.createNewSale
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.util.*

@Composable
fun ModelDetailsScreen(navController: NavHostController, itemViewModel: ItemViewModel) {
    val uiState by itemViewModel.itemUiState.collectAsState()
    // Scope
    val coroutineScope = rememberCoroutineScope()
    // Snack bar
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarColor = remember { mutableStateOf(AppColors.primaryColor) }
    // Scroll state
    val scrollState = rememberScrollState()

    MaterialTheme {
        // Scaffold
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        backgroundColor = snackBarColor.value,
                        contentColor = MaterialTheme.colors.onPrimary,
                        elevation = 8.dp
                    )
                }
            },
            backgroundColor = AppColors.backgroundColor
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (uiState.selectedItem?.images?.isNotEmpty() == true) {
                    // Image Gallery Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        elevation = 4.dp,
                        backgroundColor = AppColors.surfaceColor,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Title
                            Text(
                                text = uiState.selectedItem?.item?.name ?: "Product Details",
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.textOnPrimaryColor,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Pager status with indicator
                            val pagerState = rememberPagerState(pageCount = { uiState.selectedItem!!.images.size })

                            // Image counter
                            Text(
                                text = "${pagerState.currentPage + 1}/${uiState.selectedItem!!.images.size}",
                                style = MaterialTheme.typography.caption,
                                color = AppColors.textOnBackgroundSecondaryColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Horizontal pager with improved image display
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .background(
                                        color = AppColors.secondaryBackgroundColor,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize(),
                                ) { page ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp)
                                    ) {
                                        Image(
                                            painter = BitmapPainter(decodeBase64ToBitmap(uiState.selectedItem!!.images[page].base64Image!!)),
                                            contentDescription = uiState.selectedItem!!.item.description,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Horizontal dots indicator
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(pagerState.pageCount) { iteration ->
                                    val color = if (pagerState.currentPage == iteration)
                                        AppColors.accentColor else AppColors.secondaryColor.copy(alpha = 0.5f)
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(8.dp)
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.scrollToPage(iteration)
                                                }
                                            }
                                    )
                                }
                            }

                            // Navigation buttons with improved styling
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // "Previous" button
                                OutlinedButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (pagerState.currentPage > 0) {
                                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                            } else {
                                                // Loop to the last page
                                                pagerState.animateScrollToPage(pagerState.pageCount - 1)
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        backgroundColor = AppColors.primaryColor.copy(alpha = 0.2f),
                                        contentColor = AppColors.textOnPrimaryColor
                                    ),
                                    border = BorderStroke(0.dp, Color.Transparent)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Previous",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Previous")
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // "Next" button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (pagerState.currentPage < pagerState.pageCount - 1) {
                                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                            } else {
                                                // Loop to the first page
                                                pagerState.animateScrollToPage(0)
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = AppColors.primaryColor,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text("Next")
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Next",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Product info block
                    infoBlock(uiState)

                    // Sale view with updated styling
                    ModelSale(uiState.selectedItem!!.item.itemId, coroutineScope, snackBarColor, snackbarHostState)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Back button with improved styling
                Button(
                    onClick = { navController.navigate("main_app_view") },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.secondaryColor,
                        contentColor = AppColors.textOnSecondaryColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Return to Products", style = MaterialTheme.typography.button)
                    }
                }
            }
        }
    }
}

@Composable
private fun infoBlock(uiState: ItemUiState) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "${uiState.selectedItem!!.item.name}",
            color = AppColors.textOnBackgroundColor,
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${uiState.selectedItem.item.description}",
            color = AppColors.textOnBackgroundSecondaryColor,
            modifier = Modifier.padding(top = 8.dp)
        )
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Poster:",
            color = AppColors.textOnBackgroundColor,
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${uiState.selectedItem.person?.name}",
            color = AppColors.textOnBackgroundColor,
            modifier = Modifier.padding(top = 4.dp),
        )
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Additional info:",
            color = AppColors.textOnBackgroundColor,
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Published on: ${uiState.selectedItem.item.postDate}",
            color = AppColors.textOnBackgroundColor,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
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
    // Currency format
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cost field
        CurrencyTextField(
            value = cost,
            onValueChange = { cost = it },
            label = "Cost"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Price field
        CurrencyTextField(
            value = price,
            onValueChange = { price = it },
            label = "Price"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Summary
        if (cost.isNotEmpty() || price.isNotEmpty()) {
            BenefitSummary(
                cost = cost,
                price = price,
                currencyFormat = currencyFormat
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppColors.primaryColor,
            contentColor = AppColors.textOnPrimaryColor
        ),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            scope.launch {
                try {
                    val costDecimal = cost.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    val priceDecimal = price.toBigDecimalOrNull() ?: BigDecimal.ZERO

                    val saleDto = SaleDto(
                        cost = costDecimal,
                        price = priceDecimal,
                        itemId = itemId,
                        date = OffsetDateTime.now()
                    )

                    val serverResponse = createNewSale(saleDto)
                    color.value = if (serverResponse.success) AppColors.successColor else AppColors.errorColor
                    snackbarHostState.showSnackbar(
                        message = serverResponse.data,
                        duration = SnackbarDuration.Short
                    )

                    // Limpiar campos después de éxito
                    if (serverResponse.success) {
                        cost = ""
                        price = ""
                    }
                } catch (e: Exception) {
                    color.value = AppColors.errorColor
                    snackbarHostState.showSnackbar(
                        message = "Error: ${e.message ?: "Unknown error"}",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Add sale",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Sale", fontWeight = FontWeight.Medium)
        }
    }
}


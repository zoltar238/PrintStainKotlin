package org.example.project.ui.main.model

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.ui.AppColors
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.ReturnButton
import org.example.project.ui.component.SnackBarComponent
import org.example.project.util.imageSelector
import org.example.project.viewModel.ItemViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.image_placeholder_3x
import java.io.File

@OptIn(ExperimentalLayoutApi::class, ExperimentalResourceApi::class)
@Composable
fun ModelNewScreen(
    previousRoute: String,
    itemViewModel: ItemViewModel,
    navController: NavHostController,
) {
    val itemUiState by itemViewModel.itemUiState.collectAsState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarColor = remember { mutableStateOf(AppColors.primaryColor) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageList by remember { mutableStateOf(MutableList(8) { "" }) }

    LaunchedEffect(itemUiState.success) {
        snackBarColor.value = if (itemUiState.success) AppColors.successColor else AppColors.errorColor
    }

    LaunchedEffect(itemUiState.response) {
        if (!itemUiState.isLoading && itemUiState.response != null && itemUiState.response != "Item selected successfully") {
            snackbarHostState.showSnackbar(
                message = itemUiState.response!!,
                duration = SnackbarDuration.Short
            )
        }
    }

    MaterialTheme(
        colors = Colors(
            primary = AppColors.primaryColor,
            primaryVariant = AppColors.secondaryColor,
            secondary = AppColors.secondaryColor,
            secondaryVariant = AppColors.tertiaryColor,
            background = AppColors.backgroundColor,
            surface = AppColors.surfaceColor,
            error = AppColors.errorColor,
            onPrimary = AppColors.textOnPrimaryColor,
            onSecondary = AppColors.textOnSecondaryColor,
            onBackground = AppColors.textOnBackgroundColor,
            onSurface = AppColors.textOnBackgroundColor,
            onError = AppColors.textOnBackgroundColor,
            isLight = true
        )
    ) {
        if (itemUiState.isLoading) LoadingIndicator()

        SnackBarComponent(
            snackbarHostState = snackbarHostState,
            snackBarColor = snackBarColor
        )

        Scaffold(
            backgroundColor = AppColors.backgroundColor,
            floatingActionButtonPosition = FabPosition.Start,
            floatingActionButton = {
                ReturnButton(
                    navController = navController,
                    navigationRoute = previousRoute
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .background(AppColors.backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add new model",
                    style = MaterialTheme.typography.h4.copy(color = AppColors.textOnBackgroundColor),
                    modifier = Modifier.padding(all = 16.dp)
                )

                LazyRow(
                    state = scrollState,
                    modifier = Modifier
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    scrollState.scrollBy(-delta)
                                }
                            }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(count = imageList.size, key = { index -> imageList[index] + index }) { i ->
                        Card(
                            modifier = Modifier.size(200.dp),
                            shape = RoundedCornerShape(30.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                        ) {
                            Image(
                                painter = if (imageList[i].isNotEmpty()) {
                                    BitmapPainter(
                                        File(imageList[i]).inputStream().readAllBytes().decodeToImageBitmap()
                                    )
                                } else {
                                    painterResource(Res.drawable.image_placeholder_3x)
                                },
                                contentDescription = "User selected image",
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        shape = RoundedCornerShape(30.dp),
                                        border = BorderStroke(2.dp, AppColors.accentColor)
                                    )
                                    .clickable {
                                        imageList = imageList.toMutableList().apply {
                                            this[i] = imageSelector().toString()
                                        }
                                    }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    label = { Text("Name", color = AppColors.textOnBackgroundColor) },
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.textOnBackgroundColor,
                        focusedBorderColor = AppColors.primaryColor,
                        unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                        cursorColor = AppColors.primaryColor
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    label = { Text("Description", color = AppColors.textOnBackgroundColor) },
                    shape = RoundedCornerShape(8.dp),
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = AppColors.textOnBackgroundColor,
                        focusedBorderColor = AppColors.primaryColor,
                        unfocusedBorderColor = AppColors.textOnBackgroundSecondaryColor,
                        cursorColor = AppColors.primaryColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Implement save logic here
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = name.isNotEmpty() && description.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.primaryColor,
                        contentColor = AppColors.textOnPrimaryColor,
                        disabledBackgroundColor = AppColors.textOnBackgroundSecondaryColor,
                        disabledContentColor = AppColors.surfaceColor
                    )
                ) {
                    Text("Save model")
                }
            }
        }
    }
}
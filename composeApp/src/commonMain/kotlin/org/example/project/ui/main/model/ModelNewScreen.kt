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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.ui.AppColors
import org.example.project.ui.component.LoadingIndicator
import org.example.project.ui.component.MessageToaster
import org.example.project.ui.component.ReturnButton
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.util.imageSelector
import org.example.project.util.urlImageToBitmap
import org.example.project.viewModel.ItemViewModel
import org.jetbrains.compose.resources.painterResource
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.image_placeholder_3x
import java.io.File

@Composable
fun ModelNewScreen(
    previousRoute: String,
    itemViewModel: ItemViewModel,
    navController: NavHostController,
    option: String,
) {
    val itemUiState by itemViewModel.itemUiState.collectAsState()
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var name by remember { mutableStateOf(if (option != "new") itemUiState.selectedItem?.item?.name else "") }
    var description by remember { mutableStateOf(if (option != "new") itemUiState.selectedItem?.item?.description else "") }

    var imageBitmapList: List<ImageBitmap> by remember {
        mutableStateOf(
            if (option == "new") {
                List(8) { ImageBitmap(1, 1) }
            } else {
                // Load images from the selected item and fill the list with placeholders
                itemUiState.selectedItem?.images?.map { decodeBase64ToBitmap(it.base64Image!!) }?.take(8)?.let {
                    it + List(8 - it.size) { ImageBitmap(1, 1) }
                }!!
            }
        )
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
        // Toaster
        MessageToaster(
            messageEvent = itemUiState.messageEvent,
            success = itemUiState.success,
            onMessageConsumed = { itemViewModel.consumeMessage() }
        )

        // Loading indicator
        if (itemUiState.isLoading) LoadingIndicator()

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
                    text = if (option == "new") "Add new model" else "Modify model",
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
                    items(count = imageBitmapList.size) { i ->
                        Card(
                            modifier = Modifier.size(200.dp),
                            shape = RoundedCornerShape(30.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                        ) {
                            // Delete X on top
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .zIndex(3f),
                                    onClick = {
                                        // Remove image from the list
                                        imageBitmapList = imageBitmapList.toMutableList().apply {
                                            this[i] = ImageBitmap(1, 1)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete image",
                                        tint = AppColors.accentColor
                                    )
                                }
                                Image(
                                    if (imageBitmapList[i].width > 1) {
                                        BitmapPainter(imageBitmapList[i])
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
                                            val imageUrl = imageSelector().toString()
                                            if (imageUrl.isNotEmpty() && File(imageUrl).exists()) {
                                                imageBitmapList = imageBitmapList.toMutableList().apply {
                                                    this[i] = urlImageToBitmap(imageUrl)
                                                }
                                            }
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name!!,
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
                    value = description!!,
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

                // Save model button
                Button(
                    onClick = {
                        // Create or modify item
                        if (option == "new") {
                            itemViewModel.createItem(
                                name = name!!,
                                description = description!!,
                                images = imageBitmapList
                            )
                        } else {
                            itemViewModel.modifyItem(
                                name = name!!,
                                description = description!!,
                                images = imageBitmapList
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = name!!.isNotEmpty() && description!!.isNotEmpty() && imageBitmapList.any { it.height > 1 },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppColors.primaryColor,
                        contentColor = AppColors.textOnPrimaryColor,
                        disabledBackgroundColor = AppColors.textOnBackgroundSecondaryColor,
                        disabledContentColor = AppColors.surfaceColor
                    )
                ) {
                    if (option == "new") {
                        Text("Create model")
                    } else {
                        Text("Modify model")
                    }
                }
            }
        }
    }
}
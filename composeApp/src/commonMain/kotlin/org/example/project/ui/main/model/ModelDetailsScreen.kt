package org.example.project.ui.main.model

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import kotlinx.coroutines.launch
import org.example.project.model.dto.FileDto
import org.example.project.model.tree.TreeNode
import org.example.project.ui.AppColors
import org.example.project.ui.component.*
import org.example.project.util.decodeBase64ToBitmap
import org.example.project.viewModel.ItemUiState
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.SaleViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@Composable
fun ModelDetailsScreen(
    navController: NavHostController,
    itemViewModel: ItemViewModel,
    saleViewModel: SaleViewModel,
    previousRoute: String,
) {
    val itemUiState by itemViewModel.itemUiState.collectAsState()
    val saleUiState by saleViewModel.saleUiState.collectAsState()
    // Scope
    val coroutineScope = rememberCoroutineScope()
    // Scroll state
    val scrollState = rememberScrollState()

    LaunchedEffect(saleUiState.messageEvent?.message) {
        if (saleUiState.messageEvent?.message == "Sale created successfully") {
            itemViewModel.updateItems()
        }
    }

    MaterialTheme {
        Scaffold(
            floatingActionButtonPosition = FabPosition.Start,
            floatingActionButton = {
                ReturnButton(
                    navController = navController,
                    navigationRoute = previousRoute
                )
            },
            backgroundColor = AppColors.backgroundColor
        ) {
            // Loading indicator
            if (itemUiState.isLoading) {
                LoadingIndicator()
            }
            // Toaster
            MessageToaster(
                messageEvent = saleUiState.messageEvent,
                success = saleUiState.success,
                onMessageConsumed = { saleViewModel.consumeMessage() }
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (itemUiState.selectedItem?.images?.isNotEmpty() == true) {
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
                                text = itemUiState.selectedItem?.item?.name ?: "Product Details",
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.textOnPrimaryColor,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Pager status with indicator
                            val pagerState = rememberPagerState(pageCount = { itemUiState.selectedItem!!.images.size })

                            // Image counter
                            Text(
                                text = "${pagerState.currentPage + 1}/${itemUiState.selectedItem!!.images.size}",
                                style = MaterialTheme.typography.caption,
                                color = AppColors.textOnBackgroundSecondaryColor,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Horizontal pager for images
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
                                            painter = BitmapPainter(decodeBase64ToBitmap(itemUiState.selectedItem!!.images[page].base64Image!!)),
                                            contentDescription = itemUiState.selectedItem!!.item.description,
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
                                            pagerState.animateScrollToPage((pagerState.currentPage + 1) % pagerState.pageCount)
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
                    infoBlock(itemUiState)

                    // Sale view with updated styling
                    ModelSale(
                        itemUiState.selectedItem!!.item.itemId,
                        saleViewModel
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
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
        Divider(
            color = Color.Gray,
            thickness = 2.dp,
            modifier = Modifier.padding(top = 8.dp)
        )
        FileStructureDetail(modelName = uiState.selectedItem!!.item!!.name!!)
    }
}

// File structure detail view
@Composable
fun FileStructureDetail(modelName: String) {
    var isHidden by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val rotation by animateFloatAsState(
        targetValue = if (isHidden) 180f else 0f,
        animationSpec = tween(200, easing = LinearEasing),
    )

    // File structure tree
    var fileStructureTree by remember {
        mutableStateOf(
            TreeNode(
                FileDto(
                    fileName = modelName,
                    fileType = "directory"
                )
            )
        )
    }

    // Crear una estructura de directorios más extensa
    val srcFolder = TreeNode(FileDto(fileName = "src", fileType = "directory"))
    val mainFolder = TreeNode(FileDto(fileName = "main", fileType = "directory"))
    val testFolder = TreeNode(FileDto(fileName = "test", fileType = "directory"))

    // Estructura dentro de main
    val javaFolder = TreeNode(FileDto(fileName = "java", fileType = "directory"))
    val resourcesFolder = TreeNode(FileDto(fileName = "resources", fileType = "directory"))

    // Archivos en resources
    resourcesFolder.addChild(TreeNode(FileDto(fileName = "application.properties", fileType = "file")))
    resourcesFolder.addChild(TreeNode(FileDto(fileName = "log4j.xml", fileType = "file")))

    // Estructura dentro de java
    val comFolder = TreeNode(FileDto(fileName = "com", fileType = "directory"))
    val exampleFolder = TreeNode(FileDto(fileName = "example", fileType = "directory"))
    val projectFolder = TreeNode(FileDto(fileName = "project", fileType = "directory"))

    // Agregar archivos Java
    projectFolder.addChild(TreeNode(FileDto(fileName = "Main.java", fileType = "file")))
    projectFolder.addChild(TreeNode(FileDto(fileName = "Controller.java", fileType = "file")))
    projectFolder.addChild(TreeNode(FileDto(fileName = "Service.java", fileType = "file")))
    projectFolder.addChild(TreeNode(FileDto(fileName = "Repository.java", fileType = "file")))

    // Agregar modelos
    val modelFolder = TreeNode(FileDto(fileName = "model", fileType = "directory"))
    modelFolder.addChild(TreeNode(FileDto(fileName = "User.java", fileType = "file")))
    modelFolder.addChild(TreeNode(FileDto(fileName = "Product.java", fileType = "file")))
    projectFolder.addChild(modelFolder)

    // Configurar la estructura completa
    exampleFolder.addChild(projectFolder)
    comFolder.addChild(exampleFolder)
    javaFolder.addChild(comFolder)
    mainFolder.addChild(javaFolder)
    mainFolder.addChild(resourcesFolder)

    // Estructura dentro de test
    val testJavaFolder = TreeNode(FileDto(fileName = "java", fileType = "directory"))
    testJavaFolder.addChild(TreeNode(FileDto(fileName = "MainTest.java", fileType = "file")))
    testJavaFolder.addChild(TreeNode(FileDto(fileName = "RepositoryTest.java", fileType = "file")))
    testFolder.addChild(testJavaFolder)

    // Agregar a la estructura principal
    srcFolder.addChild(mainFolder)
    srcFolder.addChild(testFolder)
    fileStructureTree.addChild(srcFolder)

    // Agregar archivos de configuración del proyecto
    fileStructureTree.addChild(TreeNode(FileDto(fileName = "pom.xml", fileType = "file")))
    fileStructureTree.addChild(TreeNode(FileDto(fileName = "README.md", fileType = "file")))
    fileStructureTree.addChild(TreeNode(FileDto(fileName = ".gitignore", fileType = "file")))




    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(
                    text = "File structure details will be displayed here",
                    color = AppColors.textOnBackgroundColor
                )
                IconButton(
                    onClick = {
                        println("Show stuff")
                        isHidden = !isHidden
                    },
                    content = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            tint = AppColors.textOnBackgroundColor,
                            contentDescription = "Info icon",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                )
            }
            if (!isHidden) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Opps, looks like this model has no files, upload some with the button below.")
                        Button(
                            onClick = {
                                scope.launch {
//                                    val file = FileKit.openFilePicker(mode = FileKitMode.Multiple(), title = "Select model files",  )
                                    val file = FileKit.openDirectoryPicker(title = "Select model directory")
                                    println("${file}")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload Files")
                        }
                        fileStructureTree.forEachDepthFirstText("-")
                    }
                }
            }
        }
    }
}


@Composable
fun ModelSale(
    itemId: Long,
    saleViewModel: SaleViewModel,
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
            val costDecimal = cost.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val priceDecimal = price.toBigDecimalOrNull() ?: BigDecimal.ZERO

            saleViewModel.createSale(
                cost = costDecimal,
                price = priceDecimal,
                itemId = itemId
            )
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


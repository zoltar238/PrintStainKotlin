package org.example.project.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.ui.AppColors
import org.example.project.ui.main.model.ModelsScreen
import org.example.project.ui.main.sale.SalesScreen
import org.example.project.viewModel.ItemViewModel
import org.example.project.viewModel.SaleViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MainScreen(navController: NavHostController, itemViewModel: ItemViewModel, saleViewModel: SaleViewModel) {
    var selectedView by remember { mutableStateOf("Models") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val saleUiState by saleViewModel.saleUiState.collectAsState()
    val itemUiState by itemViewModel.itemUiState.collectAsState()


    LaunchedEffect(Unit) {
        if (saleUiState.sales.isEmpty()) {
            saleViewModel.getAllSales()
        }
        if (itemUiState.items.isEmpty()) {
            itemViewModel.getAllItems()
        }
    }

    Surface(
        color = AppColors.backgroundColor
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .background(AppColors.surfaceColor)
                        .padding(16.dp)
                ) {
                    Text(
                        "PrintStain Menu",
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = AppColors.textOnBackgroundColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 16.dp),
                        thickness = DividerDefaults.Thickness, color = AppColors.textOnBackgroundSecondaryColor
                    )
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Sales Icon",
                                tint = if (selectedView == "Sales") AppColors.textOnPrimaryColor else AppColors.textOnBackgroundColor
                            )
                        },
                        label = { Text("Sales") },
                        selected = selectedView == "Sales",
                        onClick = {
                            selectedView = "Sales"
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = AppColors.primaryColor,
                            unselectedContainerColor = AppColors.surfaceColor,
                            selectedTextColor = AppColors.textOnPrimaryColor,
                            unselectedTextColor = AppColors.textOnBackgroundColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.ViewInAr,
                                contentDescription = "Models Icon",
                                tint = if (selectedView == "Models") AppColors.textOnPrimaryColor else AppColors.textOnBackgroundColor
                            )
                        },
                        label = { Text("Models") },
                        selected = selectedView == "Models",
                        onClick = {
                            selectedView = "Models"
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = AppColors.primaryColor,
                            unselectedContainerColor = AppColors.surfaceColor,
                            selectedTextColor = AppColors.textOnPrimaryColor,
                            unselectedTextColor = AppColors.textOnBackgroundColor
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings Icon",
                                tint = if (selectedView == "Settings") AppColors.textOnPrimaryColor else AppColors.textOnBackgroundColor
                            )
                        },
                        label = { Text("Settings") },
                        selected = selectedView == "Settings",
                        onClick = {
                            selectedView = "Settings"
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = AppColors.primaryColor,
                            unselectedContainerColor = AppColors.surfaceColor,
                            selectedTextColor = AppColors.textOnPrimaryColor,
                            unselectedTextColor = AppColors.textOnBackgroundColor
                        )
                    )
                }
            }
        ) {

            Scaffold(
                containerColor = AppColors.backgroundColor,
                contentColor = AppColors.textOnBackgroundColor,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "PrintStain - $selectedView",
                                color = AppColors.textOnPrimaryColor
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu",
                                    tint = AppColors.textOnPrimaryColor
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = AppColors.tertiaryColor
                        )
                    )
                },
                content = { paddingValues ->
                    Surface(
                        modifier = Modifier.padding(paddingValues),
                        color = AppColors.backgroundColor
                    ) {
                        when (selectedView) {
                            "Sales" -> SalesScreen(saleViewModel, itemViewModel)
                            "Models" -> ModelsScreen(
                                navController = navController,
                                itemViewModel = itemViewModel,
                            )

                            "Settings" -> SettingsView(
                                navController = navController
                            )
                        }
                    }
                }
            )
        }
    }
}
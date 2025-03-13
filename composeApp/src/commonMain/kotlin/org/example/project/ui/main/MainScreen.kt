package org.example.project.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.example.project.persistence.preferences.PreferencesManager
import org.example.project.service.ItemViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MainScreen(navController: NavHostController, itemViewModel: ItemViewModel) {

    MaterialTheme {
        // Estado que controla el menú seleccionado y el estado del drawer
        var selectedView by remember { mutableStateOf("Models") }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        var username by remember { mutableStateOf<String?>(null) }
        scope.launch { username = PreferencesManager.getUsername() }

        // ModalNavigationDrawer as sidebar
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text("Drawer title", modifier = Modifier.padding(16.dp))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        label = { Text(text = "Sales") },
                        selected = selectedView == "Sales",
                        onClick = { selectedView = "Sales" }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Models") },
                        selected = selectedView == "Models",
                        onClick = { selectedView = "Models" }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Ner training") },
                        selected = selectedView == "Ner",
                        onClick = { selectedView = "Ner" }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "Settings") },
                        selected = selectedView == "Settings",
                        onClick = { selectedView = "Settings" }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Menu, // Icono de menú estándar de Material
                                        contentDescription = "Abrir menú"
                                    )
                                }

                            )
                        },
                        title = { Text("PrintStain - $selectedView") },
                        backgroundColor = MaterialTheme.colors.primary
                    )
                },
                content = {
                    // Pantalla principal según la vista seleccionada
                    when (selectedView) {
                        "Sales" -> SalesScreen()
                        // Send model status and collected models
                        "Models" -> ModelsScreen(
                            navController = navController,
                            viewModel = itemViewModel,
                        )
                        "Settings" -> SettingsView()
                        "Ner" -> NerTrainingView()
                    }
                }
            )
        }
    }
}


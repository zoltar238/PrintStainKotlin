package org.example.project.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MainAppView() {
    MaterialTheme {
        MaterialTheme {
            // Estado que controla el menú seleccionado y el estado del drawer
            var selectedView by remember { mutableStateOf("Sales") }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            // ModalNavigationDrawer que contiene la barra lateral y la pantalla principal
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
                            "Sales" -> SalesView(Modifier.padding())
                            //"Reports" -> ReportsView(Modifier.padding(innerPadding))
                            //"Settings" -> SettingsView(Modifier.padding(innerPadding))
                        }
                    }
                )
            }
        }
    }
}

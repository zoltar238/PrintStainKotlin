package org.example.project.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.persistence.preferences.PreferencesDaoImpl
import org.example.project.ui.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView() {
    val scope = rememberCoroutineScope()
    var darkMode by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        darkMode = PreferencesDaoImpl.getDarkMode() ?: false
    }

    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.backgroundColor)
        ) {
            Scaffold(
                containerColor = AppColors.backgroundColor,
                contentColor = AppColors.textOnBackgroundColor
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Appearance Section
                    Text(
                        "Apariencia",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textOnBackgroundColor
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.surfaceColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        // Dark mode setting
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Dark Mode",
                                style = MaterialTheme.typography.bodyLarge,
                                color = AppColors.textOnBackgroundColor
                            )
                            Switch(
                                checked = darkMode,
                                onCheckedChange = {
                                    scope.launch {
                                        darkMode = it
                                        PreferencesDaoImpl.saveDarkMode(it)
                                        AppColors.toggleDarkMode()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AppColors.primaryColor,
                                    checkedTrackColor = AppColors.primaryColor.copy(alpha = 0.5f),
                                    uncheckedThumbColor = AppColors.textOnBackgroundSecondaryColor,
                                    uncheckedTrackColor = AppColors.textOnBackgroundSecondaryColor.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Account Section
                    Text(
                        "Cuenta",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textOnBackgroundColor
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.surfaceColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Password change button
                            Button(
                                onClick = {
                                    scope.launch {
                                        PreferencesDaoImpl.deleteAllPreferences()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.primaryColor,
                                    contentColor = AppColors.textOnPrimaryColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp).padding(end = 8.dp)
                                )
                                Text("Reset Password")
                            }

                            // Delete data button
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        PreferencesDaoImpl.deleteAllPreferences()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AppColors.errorColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp).padding(end = 8.dp)
                                )
                                Text("Delete saved data")
                            }

                            // Logout button
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        PreferencesDaoImpl.deleteAllPreferences()
                                        // TODO: Navigate to login screen
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AppColors.primaryColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp).padding(end = 8.dp)
                                )
                                Text("Close session")
                            }

                            // Delete account button
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        PreferencesDaoImpl.deleteAllPreferences()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = AppColors.errorColor
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp).padding(end = 8.dp)
                                )
                                Text("Delete account")
                            }
                        }
                    }
                }
            }
        }
    }
}
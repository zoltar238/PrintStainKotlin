package org.example.project.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.project.persistence.preferences.PreferencesDaoImpl

object AppColors {

    var isDarkMode by mutableStateOf(runBlocking { PreferencesDaoImpl.getDarkMode() ?: false })
        private set

    // Función para cambiar el modo
    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    // Colores principales pastel
    val primaryColor get() = if (isDarkMode) Color(0xFF2E7D32) else Color(0xFFA5D6A7)
    val secondaryColor get() = if (isDarkMode) Color(0xFFF57C00) else Color(0xFFFFCC80)
    val tertiaryColor get() = if (isDarkMode) Color(0xFF1976D2) else Color(0xFFBBDEFB)

    // Colores para acentos y estados
    val accentColor get() = if (isDarkMode) Color(0xFF7B1FA2) else Color(0xFFCE93D8)
    val errorColor get() = if (isDarkMode) Color(0xFFD32F2F) else Color(0xFFEF9A9A)
    val warningColor get() = if (isDarkMode) Color(0xFFFDD835) else Color(0xFFFFF59D)
    val successColor get() = if (isDarkMode) Color(0xFF388E3C) else Color(0xFFC5E1A5)

    // Colores para fondos y superficies
    val backgroundColor get() = if (isDarkMode) Color(0xFF121212) else Color(0xFFE6E8FA)
    val secondaryBackgroundColor get() = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFE1F5FE)
    val surfaceColor get() = if (isDarkMode) Color(0xFF242424) else Color(0xFFFFFFFF)

    // Colores para texto
    val textOnPrimaryColor get() = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF2E7D32)
    val textOnBackgroundSecondaryColor get() = if (isDarkMode) Color(0xFF757575) else Color(0xFFB0B0B0)
    val textOnSecondaryColor get() = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF774D00)
    val textOnBackgroundColor get() = if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF37474F)
}
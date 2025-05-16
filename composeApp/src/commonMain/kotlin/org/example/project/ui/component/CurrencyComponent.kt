package org.example.project.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.example.project.ui.AppColors

// Reusable currency text field
@Composable
fun CurrencyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newText ->
            val cleanText = newText.filter { it.isDigit() || it == '.' }

            if (cleanText.isEmpty() || cleanText.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                onValueChange(cleanText)
            }
        },
        label = { Text(label) },
        placeholder = { Text("0.00") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        leadingIcon = {
            Text(
                "$",
                fontWeight = FontWeight.Bold,
                color = AppColors.textOnBackgroundSecondaryColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}
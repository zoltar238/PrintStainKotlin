package org.example.project.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.AppColors
import java.text.NumberFormat

@Composable
fun BenefitSummary(cost: String, price: String, currencyFormat: NumberFormat) {
    val costValue = cost.toDoubleOrNull() ?: 0.0
    val priceValue = price.toDoubleOrNull() ?: 0.0
    val profit = priceValue - costValue
    val profitPercentage = if (costValue > 0) (profit / costValue) * 100 else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp,
        backgroundColor = AppColors.secondaryBackgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = AppColors.textOnPrimaryColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cost:", color = AppColors.textOnBackgroundSecondaryColor)
                Text(
                    currencyFormat.format(costValue),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Price:", color = AppColors.textOnBackgroundSecondaryColor)
                Text(
                    currencyFormat.format(priceValue),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Divider(Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Profit:", color = AppColors.textOnBackgroundSecondaryColor)
                Text(
                    currencyFormat.format(profit),
                    color = if (profit >= 0) AppColors.successColor else AppColors.errorColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Margin:", color = AppColors.textOnBackgroundSecondaryColor)
                Text(
                    String.format("%.1f%%", profitPercentage),
                    color = if (profitPercentage >= 0) AppColors.successColor else AppColors.errorColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
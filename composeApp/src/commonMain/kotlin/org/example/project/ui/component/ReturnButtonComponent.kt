package org.example.project.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.example.project.ui.AppColors

@Composable
fun ReturnButton(navController: NavHostController, navigationRoute: String) {
    // Back button with improved styling
    Box(modifier =  Modifier.fillMaxSize()) {

        Button(
            onClick = { navController.navigate(navigationRoute) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColors.secondaryColor,
                contentColor = AppColors.textOnSecondaryColor
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

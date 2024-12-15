package org.example.project.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.example.project.service.getAllItems
import printstain.composeapp.generated.resources.Res
import printstain.composeapp.generated.resources.r_3x

@Composable
fun ModelsScreen() {

    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            //horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center
        ) {
            ModelCard()
            Text("Esta es la vista de modelos", modifier = Modifier.padding(16.dp))
        }
    }
}

// Model individual cards
@Composable
fun ModelCard() {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp),
        shape = RoundedCornerShape(30.dp),
        // Shadow for better visibility
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            // Imagen
            Image(
                painter = org.jetbrains.compose.resources.painterResource(Res.drawable.r_3x),
                contentDescription = null,
                contentScale = ContentScale.FillBounds, // Asegura que la imagen llena el área disponible
                modifier = Modifier.fillMaxSize()
            )

            // Caja de texto en la parte inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f)) // Fondo oscuro con transparencia
                    .align(Alignment.BottomCenter) // Alinea al fondo
                    .padding(8.dp), // Espaciado interno
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Titulo",
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.White, // Texto claro para contraste
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}


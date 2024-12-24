package org.example.project.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.example.project.controller.ItemController

@Composable
fun ModelDetailsScreen(navController: NavHostController, itemId: String?) {
    val longItemId = itemId?.toLong()
    val item = longItemId?.let { ItemController.getItemById(it) }
    MaterialTheme {

        Column {
            if (item != null) {
                Image(
                    painter = BitmapPainter(item.bitmapImages[0]),
                    contentDescription = item.description,
                    contentScale = ContentScale.FillBounds, // Asegura que la imagen llena el área disponible
                    //modifier = Modifier.fillMaxSize()
                )
            }

            Row  (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    TableCell(text = "Row Col 1", weight = 1f)
                    TableCell(text = "Row Col 2", weight = 1f)
                }
            }
            Button(onClick = {navController.navigate("main_app_view")}) {
                Text("Atrás")
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}
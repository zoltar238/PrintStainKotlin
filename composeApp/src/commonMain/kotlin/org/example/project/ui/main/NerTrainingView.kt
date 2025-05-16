package org.example.project.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NerTrainingView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = {}) {
            Text("Import data")
        }

        Box(
            modifier = Modifier
                .size(600.dp)
                .background(color = Color.Gray)
        ) {
            var text by remember { mutableStateOf("") }
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { text = it },
                label = { Text("outlined") })
        }

        Row {
            Button(onClick = {}) {
                Text("Accept")
            }

            Button(onClick = {}) {
                Text("Reject")
            }

            Button(onClick = {}) {
                Text("Ignore")
            }

            Button(onClick = {}) {
                Text("Undo")
            }
        }
    }
}
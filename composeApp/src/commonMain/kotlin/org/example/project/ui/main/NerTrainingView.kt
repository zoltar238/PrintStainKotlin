package org.example.project.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

        Button(onClick = {}){
            Text("Import data")
        }

        Box(
            modifier = Modifier
                .size(600.dp)
                .background(color = Color.Gray)
        ) {

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
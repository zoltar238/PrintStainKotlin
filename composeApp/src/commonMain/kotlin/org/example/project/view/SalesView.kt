package org.example.project.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.bar.BarChart
import com.netguru.multiplatform.charts.bar.BarChartCategory
import com.netguru.multiplatform.charts.bar.BarChartConfig
import com.netguru.multiplatform.charts.bar.BarChartData
import com.netguru.multiplatform.charts.bar.BarChartEntry
import com.netguru.multiplatform.charts.line.LineChart
import com.netguru.multiplatform.charts.line.LineChartData
import com.netguru.multiplatform.charts.line.LineChartPoint
import com.netguru.multiplatform.charts.line.LineChartSeries

@Composable
fun SalesView(modifier: Modifier = Modifier) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //XYSamplePlot(true, "hola")
            LineChartExample()
            Text("Esta es la vista de Ventas", modifier = modifier.padding(16.dp))
        }
    }
}

@Composable
fun LineChartExample() {
    val barChartData = BarChartData(
        categories = listOf(
            BarChartCategory(
                name = "Bar Chart 1",
                entries = listOf(
                    BarChartEntry(
                        x = "primary",
                        y = 17f,
                        color = Color.Yellow,
                    ),
                    BarChartEntry(
                        x = "secondary",
                        y = 30f,
                        color = Color.Red,
                    ),
                )
            ),
            BarChartCategory(
                name = "Bar Chart 2",
                entries = listOf(
                    BarChartEntry(
                        x = "primary",
                        y = -5f,
                        color = Color.Yellow,
                    ),
                    BarChartEntry(
                        x = "secondary",
                        y = -24f,
                        color = Color.Red,
                    ),
                )
            ),
        )
    )
    BarChart(
        data = barChartData,
        config = BarChartConfig(
            thickness = 14.dp,
            cornerRadius = 7.dp,
        ),
        modifier = Modifier.height(500.dp),
        animation = ChartAnimation.Sequenced(),
    )
}

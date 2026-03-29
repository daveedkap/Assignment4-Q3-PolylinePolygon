package com.example.assignment4q3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment4q3.ui.theme.Assignment4Q3Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment4Q3Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MapScreen()
                }
            }
        }
    }
}

data class NamedColor(val name: String, val color: Color)

val colorOptions = listOf(
    NamedColor("Red", Color.Red),
    NamedColor("Blue", Color.Blue),
    NamedColor("Green", Color(0xFF4CAF50)),
    NamedColor("Orange", Color(0xFFFF9800)),
    NamedColor("Purple", Color(0xFF9C27B0)),
    NamedColor("Cyan", Color.Cyan),
    NamedColor("Yellow", Color.Yellow),
    NamedColor("Black", Color.Black),
)

// Mist Trail in Yosemite National Park (approximate waypoints)
val mistTrailPoints = listOf(
    LatLng(37.7327, -119.5573),
    LatLng(37.7318, -119.5565),
    LatLng(37.7305, -119.5558),
    LatLng(37.7290, -119.5550),
    LatLng(37.7275, -119.5545),
    LatLng(37.7260, -119.5535),
    LatLng(37.7248, -119.5520),
    LatLng(37.7240, -119.5505),
    LatLng(37.7235, -119.5488),
    LatLng(37.7225, -119.5475),
    LatLng(37.7215, -119.5460),
    LatLng(37.7210, -119.5445),
)

// Polygon around the Yosemite Valley meadow area near the trail
val yosemiteMeadowPolygon = listOf(
    LatLng(37.7380, -119.5650),
    LatLng(37.7380, -119.5480),
    LatLng(37.7340, -119.5430),
    LatLng(37.7290, -119.5420),
    LatLng(37.7250, -119.5450),
    LatLng(37.7240, -119.5550),
    LatLng(37.7270, -119.5620),
    LatLng(37.7330, -119.5660),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MapScreen() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7280, -119.5530), 15f)
    }

    var polylineColor by remember { mutableStateOf(Color.Red) }
    var polylineWidth by remember { mutableFloatStateOf(8f) }
    var polygonStrokeColor by remember { mutableStateOf(Color.Blue) }
    var polygonFillColor by remember { mutableStateOf(Color(0x4D4CAF50)) }
    var showControls by remember { mutableStateOf(false) }
    var showTrailDialog by remember { mutableStateOf(false) }
    var showParkDialog by remember { mutableStateOf(false) }

    // Track which property the color picker is targeting
    var activeColorTarget by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Polyline(
                points = mistTrailPoints,
                color = polylineColor,
                width = polylineWidth,
                clickable = true,
                onClick = { showTrailDialog = true }
            )
            Polygon(
                points = yosemiteMeadowPolygon,
                strokeColor = polygonStrokeColor,
                fillColor = polygonFillColor,
                strokeWidth = 4f,
                clickable = true,
                onClick = { showParkDialog = true }
            )
        }

        Button(
            onClick = { showControls = !showControls },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(if (showControls) "Hide Controls" else "Customize")
        }

        AnimatedVisibility(
            visible = showControls,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Map Overlay Controls", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Polyline Width: ${polylineWidth.toInt()}px", fontSize = 14.sp)
                    Slider(
                        value = polylineWidth,
                        onValueChange = { polylineWidth = it },
                        valueRange = 2f..30f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ColorPickerRow(
                        label = "Polyline Color",
                        selectedColor = polylineColor,
                        expanded = activeColorTarget == "polyline",
                        onToggle = { activeColorTarget = if (activeColorTarget == "polyline") null else "polyline" },
                        onColorSelected = { polylineColor = it; activeColorTarget = null }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ColorPickerRow(
                        label = "Polygon Stroke",
                        selectedColor = polygonStrokeColor,
                        expanded = activeColorTarget == "polygonStroke",
                        onToggle = { activeColorTarget = if (activeColorTarget == "polygonStroke") null else "polygonStroke" },
                        onColorSelected = { polygonStrokeColor = it; activeColorTarget = null }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ColorPickerRow(
                        label = "Polygon Fill",
                        selectedColor = polygonFillColor,
                        expanded = activeColorTarget == "polygonFill",
                        onToggle = { activeColorTarget = if (activeColorTarget == "polygonFill") null else "polygonFill" },
                        onColorSelected = { nc ->
                            polygonFillColor = nc.copy(alpha = 0.3f)
                            activeColorTarget = null
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showTrailDialog) {
        AlertDialog(
            onDismissRequest = { showTrailDialog = false },
            title = { Text("Mist Trail") },
            text = {
                Column {
                    Text("Location: Yosemite National Park, CA")
                    Text("Length: 5.4 miles round trip")
                    Text("Difficulty: Strenuous")
                    Text("Elevation Gain: 1,000 ft")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "One of Yosemite's most iconic hikes, passing Vernal Fall " +
                                "and Nevada Fall with stunning views of the valley.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTrailDialog = false }) { Text("Close") }
            }
        )
    }

    if (showParkDialog) {
        AlertDialog(
            onDismissRequest = { showParkDialog = false },
            title = { Text("Yosemite Valley Meadow") },
            text = {
                Column {
                    Text("Area: ~1,200 acres")
                    Text("Location: Yosemite Valley Floor")
                    Text("Type: Protected Meadowland")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The meadows of Yosemite Valley provide critical habitat for " +
                                "wildlife and offer spectacular views of El Capitan, " +
                                "Half Dome, and Yosemite Falls.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showParkDialog = false }) { Text("Close") }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerRow(
    label: String,
    selectedColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 4.dp)
        ) {
            Text(label, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .border(2.dp, Color.DarkGray, CircleShape)
            )
        }

        if (expanded) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colorOptions.forEach { nc ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(nc.color)
                            .border(
                                width = if (nc.color == selectedColor || nc.color.copy(alpha = 0.3f) == selectedColor) 3.dp else 1.dp,
                                color = if (nc.color == selectedColor || nc.color.copy(alpha = 0.3f) == selectedColor) Color.White else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(nc.color) }
                    )
                }
            }
        }
    }
}

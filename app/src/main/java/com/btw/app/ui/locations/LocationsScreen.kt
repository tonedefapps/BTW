package com.btw.app.ui.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.domain.model.LocationSource
import com.btw.app.domain.model.SavedLocation
import com.btw.app.ui.theme.*

@Composable
fun LocationsScreen(
    onBack: () -> Unit,
    viewModel: LocationsViewModel = hiltViewModel()
) {
    val locations by viewModel.locations.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "back", tint = Sky)
            }
            Text("locations", style = MaterialTheme.typography.headlineSmall, color = Air)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "add location", tint = Sky)
            }
        }

        if (locations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("no locations yet", style = MaterialTheme.typography.bodyMedium, color = Sky)
                    Text("add places where pickups are expected", style = MaterialTheme.typography.bodySmall, color = Depth)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(locations) { loc ->
                    LocationRow(location = loc, onDelete = { viewModel.deleteLocation(loc.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddLocationDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { label, emoji, lat, lng, radius ->
                viewModel.addManualLocation(label, emoji, lat, lng, radius)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun LocationRow(location: SavedLocation, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Depth.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (location.emoji.isNotBlank()) Text(location.emoji, fontSize = 22.sp)
            Column {
                Text(location.label, style = MaterialTheme.typography.bodyMedium, color = Air)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ConfidencePill(location.confidence)
                    Text(
                        text = if (location.source == LocationSource.MANUAL) "manual" else "${location.visitCount} visits",
                        style = MaterialTheme.typography.labelMedium,
                        color = Depth
                    )
                }
                Text(
                    "%.5f, %.5f  •  ${location.radiusMeters.toInt()}m radius".format(location.lat, location.lng),
                    style = MaterialTheme.typography.labelMedium,
                    color = Depth
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "delete", tint = Sky)
        }
    }
}

@Composable
private fun ConfidencePill(confidence: Float) {
    val pct = (confidence * 100).toInt()
    val color = when {
        pct >= 80 -> Sky
        pct >= 40 -> Sand
        else -> Depth
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            "$pct%",
            style = MaterialTheme.typography.labelMedium,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun AddLocationDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Double, Double, Float) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var latStr by remember { mutableStateOf("") }
    var lngStr by remember { mutableStateOf("") }
    var radiusStr by remember { mutableStateOf("50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text("add location", color = Air, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BtwTextField(value = label, onValueChange = { label = it }, label = "label")
                BtwTextField(value = emoji, onValueChange = { if (it.length <= 2) emoji = it }, label = "emoji (optional)")
                BtwTextField(value = latStr, onValueChange = { latStr = it }, label = "latitude", keyboardType = KeyboardType.Decimal)
                BtwTextField(value = lngStr, onValueChange = { lngStr = it }, label = "longitude", keyboardType = KeyboardType.Decimal)
                BtwTextField(value = radiusStr, onValueChange = { radiusStr = it }, label = "radius (metres)", keyboardType = KeyboardType.Number)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val lat = latStr.toDoubleOrNull() ?: return@TextButton
                val lng = lngStr.toDoubleOrNull() ?: return@TextButton
                val radius = radiusStr.toFloatOrNull() ?: 50f
                if (label.isNotBlank()) onAdd(label.trim(), emoji.trim(), lat, lng, radius)
            }) {
                Text("add", color = Air)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("cancel", color = Sky) }
        }
    )
}

@Composable
private fun BtwTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Sky) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Air,
            unfocusedTextColor = Air,
            focusedBorderColor = Sky,
            unfocusedBorderColor = Air.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

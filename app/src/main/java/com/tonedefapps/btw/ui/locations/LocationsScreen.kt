package com.tonedefapps.btw.ui.locations

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.tonedefapps.btw.domain.model.LocationSource
import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Locale

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
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp)
    ) {
        BtwTopBar(
            title = "known locations",
            onBack = onBack,
            trailing = {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = "add location", tint = Sky)
                }
            }
        )

        if (locations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("no known locations yet", style = MaterialTheme.typography.bodyMedium, color = Air)
                    Text(
                        "add places you regularly stop — home, work, school drop-off, anywhere btw should keep watch",
                        style = MaterialTheme.typography.bodySmall,
                        color = Sky
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(locations) { loc ->
                    LocationCard(location = loc, onDelete = { viewModel.deleteLocation(loc.id) })
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
private fun LocationCard(location: SavedLocation, onDelete: () -> Unit) {
    BtwCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (location.emoji.isNotBlank()) Text(location.emoji, fontSize = 22.sp)
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(location.label, style = MaterialTheme.typography.bodyMedium, color = Air)
                    if (location.source != LocationSource.MANUAL && location.visitCount > 0) {
                        Text(
                            text = "${location.visitCount} visits",
                            style = MaterialTheme.typography.labelSmall,
                            color = Sky.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "delete", tint = Sky.copy(alpha = 0.6f))
            }
        }
    }
}


@SuppressLint("MissingPermission")
@Composable
private fun AddLocationDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Double, Double, Float) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var addressInput by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("") }
    var capturedLocation by remember { mutableStateOf<android.location.Location?>(null) }
    var resolvedAddress by remember { mutableStateOf<String?>(null) }
    var resolving by remember { mutableStateOf(false) }
    var resolveError by remember { mutableStateOf<String?>(null) }

    fun clearResolved() {
        capturedLocation = null
        resolvedAddress = null
        resolveError = null
    }

    suspend fun resolveByAddress(text: String) {
        resolving = true
        clearResolved()
        try {
            val results = withTimeout(5_000L) {
                withContext(Dispatchers.IO) {
                    if (!Geocoder.isPresent()) return@withContext emptyList()
                    @Suppress("DEPRECATION")
                    Geocoder(context, Locale.getDefault()).getFromLocationName(text, 1) ?: emptyList()
                }
            }
            val first = results.firstOrNull()
            if (first != null) {
                capturedLocation = android.location.Location("geocoded").apply {
                    latitude = first.latitude
                    longitude = first.longitude
                }
                val addr = listOfNotNull(
                    listOfNotNull(first.subThoroughfare, first.thoroughfare).joinToString(" ").ifBlank { null },
                    first.locality
                ).joinToString(", ").ifBlank { first.getAddressLine(0) ?: text }
                resolvedAddress = addr
                if (label.isBlank()) label = addr
            } else {
                resolveError = "address not found — try being more specific"
            }
        } catch (_: Exception) {
            resolveError = "couldn't look up that address"
        }
        resolving = false
    }

    suspend fun resolveByGps() {
        resolving = true
        clearResolved()
        val loc = suspendCancellableCoroutine<android.location.Location?> { cont ->
            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { cont.resumeWith(Result.success(it)) }
                .addOnFailureListener { cont.resumeWith(Result.success(null)) }
        }
        if (loc == null) { resolveError = "couldn't get your location"; resolving = false; return }
        capturedLocation = loc

        val addr = if (Geocoder.isPresent()) {
            try {
                withTimeout(3_000L) {
                    withContext(Dispatchers.IO) {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            suspendCancellableCoroutine { c ->
                                geocoder.getFromLocation(loc.latitude, loc.longitude, 1) { list -> c.resumeWith(Result.success(list)) }
                            }
                        } else {
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocation(loc.latitude, loc.longitude, 1) ?: emptyList()
                        }
                        results.firstOrNull()?.let { a ->
                            listOfNotNull(
                                listOfNotNull(a.subThoroughfare, a.thoroughfare).joinToString(" ").ifBlank { null },
                                a.locality
                            ).joinToString(", ")
                        }
                    }
                }
            } catch (_: Exception) { null }
        } else null

        resolvedAddress = addr
        if (addr != null) addressInput = addr
        if (label.isBlank()) label = addr ?: "my location"
        resolving = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text("add a known location", color = Air, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Address entry + find button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BtwTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it; clearResolved() },
                        label = "address or place name",
                        modifier = Modifier.weight(1f)
                    )
                    if (resolving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Sky)
                    } else {
                        TextButton(
                            onClick = { if (addressInput.isNotBlank()) scope.launch { resolveByAddress(addressInput) } },
                            enabled = addressInput.isNotBlank(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text("find", color = if (addressInput.isNotBlank()) Sky else Sky.copy(alpha = 0.3f), fontSize = 14.sp)
                        }
                    }
                }

                // Use current location link
                TextButton(
                    onClick = { scope.launch { resolveByGps() } },
                    enabled = !resolving,
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Outlined.MyLocation, contentDescription = null, tint = Sky.copy(alpha = if (resolving) 0.4f else 1f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("use my current location", color = Sky.copy(alpha = if (resolving) 0.4f else 1f), fontSize = 13.sp, fontFamily = DmSans)
                }

                // Resolved status
                when {
                    capturedLocation != null -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Outlined.MyLocation, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(14.dp))
                        Text(resolvedAddress ?: "location confirmed", style = MaterialTheme.typography.bodySmall, color = SafeGreen)
                    }
                    resolveError != null -> Text(resolveError!!, style = MaterialTheme.typography.bodySmall, color = AlertRed)
                    else -> {}
                }

                // Name fields — always visible so user can fill in parallel
                BtwTextField(value = label, onValueChange = { label = it }, label = "give it a name (e.g. home, work)")
                BtwTextField(value = emoji, onValueChange = { if (it.length <= 2) emoji = it }, label = "emoji (optional)")
            }
        },
        confirmButton = {
            val canAdd = capturedLocation != null && label.isNotBlank()
            TextButton(
                onClick = {
                    val loc = capturedLocation ?: return@TextButton
                    if (label.isNotBlank()) onAdd(label.trim(), emoji.trim(), loc.latitude, loc.longitude, 150f)
                },
                enabled = canAdd
            ) { Text("add", color = if (canAdd) Air else Sky.copy(alpha = 0.4f)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("cancel", color = Sky) }
        }
    )
}

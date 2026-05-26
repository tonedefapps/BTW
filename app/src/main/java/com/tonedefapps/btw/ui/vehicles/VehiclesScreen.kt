package com.tonedefapps.btw.ui.vehicles

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.IntentSender
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.tonedefapps.btw.domain.model.Vehicle
import com.tonedefapps.btw.domain.model.isLocationOnly
import com.tonedefapps.btw.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VehiclesScreen(
    onBack: () -> Unit,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val context = LocalContext.current
    var showNoBtDialog by remember { mutableStateOf(false) }
    var noBtName by remember { mutableStateOf("") }

    val pairingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@rememberLauncherForActivityResult
            @Suppress("DEPRECATION")
            val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getParcelableExtra(
                    CompanionDeviceManager.EXTRA_ASSOCIATION,
                    android.companion.AssociationInfo::class.java
                )?.associatedDevice?.bluetoothDevice
            } else {
                data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
            }
            device?.let { viewModel.onDevicePaired(it) }
        }
    }

    val btPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        else emptyList()
    }
    val btPermissionsState = rememberMultiplePermissionsState(btPermissions)
    var pendingScan by remember { mutableStateOf(false) }

    fun doScan() {
        val cdm = context.getSystemService(CompanionDeviceManager::class.java)
        val request = AssociationRequest.Builder()
            .addDeviceFilter(BluetoothDeviceFilter.Builder().build())
            .setSingleDevice(false)
            .build()
        try {
            cdm.associate(request, object : CompanionDeviceManager.Callback() {
                @Suppress("OVERRIDE_DEPRECATION")
                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    pairingLauncher.launch(IntentSenderRequest.Builder(chooserLauncher).build())
                }
                override fun onFailure(error: CharSequence?) {
                    viewModel.onPairingFailed()
                }
            }, null)
        } catch (_: SecurityException) {
            viewModel.onPairingFailed()
        }
    }

    fun startPairing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !btPermissionsState.allPermissionsGranted) {
            pendingScan = true
            btPermissionsState.launchMultiplePermissionRequest()
        } else {
            doScan()
        }
    }

    LaunchedEffect(pendingScan, btPermissionsState.allPermissionsGranted) {
        if (pendingScan && btPermissionsState.allPermissionsGranted) {
            pendingScan = false
            doScan()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp)
    ) {
        BtwTopBar(title = "vehicles", onBack = onBack)

        if (vehicles.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("no vehicles added", style = MaterialTheme.typography.bodyLarge, color = Air)
                    Text(
                        "pair a bluetooth device or add a location-only vehicle",
                        style = MaterialTheme.typography.bodySmall,
                        color = Sky
                    )
                }
            }
        } else {
            BtwSectionHeader("vehicles")
            BtwCard {
                vehicles.forEachIndexed { index, vehicle ->
                    if (index > 0) BtwRowDivider()
                    VehicleRow(vehicle = vehicle, onDelete = { viewModel.deleteVehicle(vehicle.id) })
                }
            }
            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        BtwPrimaryButton(text = "pair a bluetooth vehicle", onClick = ::startPairing)
        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = { noBtName = ""; showNoBtDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("my car doesn't have bluetooth", color = Sky, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(16.dp))
    }

    if (showNoBtDialog) {
        AlertDialog(
            onDismissRequest = { showNoBtDialog = false },
            containerColor = Depth,
            title = { Text("name your vehicle", color = Air, style = MaterialTheme.typography.titleLarge) },
            text = {
                BtwTextField(
                    value = noBtName,
                    onValueChange = { noBtName = it },
                    label = "vehicle name (e.g. ford f-150)"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (noBtName.isNotBlank()) {
                        viewModel.addLocationOnlyVehicle(noBtName)
                        showNoBtDialog = false
                    }
                }) { Text("add", color = Air) }
            },
            dismissButton = {
                TextButton(onClick = { showNoBtDialog = false }) { Text("cancel", color = Sky) }
            }
        )
    }
}

@Composable
private fun VehicleRow(vehicle: Vehicle, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (vehicle.isLocationOnly) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Sky, modifier = Modifier.size(18.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(vehicle.name, style = MaterialTheme.typography.bodyLarge, color = Air)
                Text(
                    text = if (vehicle.isLocationOnly) "watches known locations" else vehicle.bluetoothAddress!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = Sky
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "remove", tint = Sky.copy(alpha = 0.6f))
        }
    }
}

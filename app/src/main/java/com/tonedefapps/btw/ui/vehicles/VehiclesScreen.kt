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
import com.tonedefapps.btw.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VehiclesScreen(
    onBack: () -> Unit,
    viewModel: VehiclesViewModel = hiltViewModel()
) {
    val vehicles by viewModel.vehicles.collectAsState()
    val context = LocalContext.current

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
                    Text("no vehicles paired", style = MaterialTheme.typography.bodyLarge, color = Air)
                    Text(
                        "pair the bluetooth audio system or hands-free kit in your car",
                        style = MaterialTheme.typography.bodySmall,
                        color = Sky
                    )
                }
            }
        } else {
            BtwSectionHeader("paired vehicles")
            BtwCard {
                vehicles.forEachIndexed { index, vehicle ->
                    if (index > 0) BtwRowDivider()
                    VehicleRow(vehicle = vehicle, onDelete = { viewModel.deleteVehicle(vehicle.id) })
                }
            }
            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        BtwPrimaryButton(text = "pair a vehicle", onClick = ::startPairing)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "pair the car's audio system or hands-free bluetooth kit",
            style = MaterialTheme.typography.bodySmall,
            color = Sky,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(24.dp))
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
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            Text(vehicle.name, style = MaterialTheme.typography.bodyLarge, color = Air)
            Text(vehicle.bluetoothAddress, style = MaterialTheme.typography.bodySmall, color = Sky)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "remove", tint = Sky.copy(alpha = 0.6f))
        }
    }
}

package com.tonedefapps.btw.ui.setup

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.tonedefapps.btw.ui.onboarding.BtwButton
import com.tonedefapps.btw.ui.onboarding.Wordmark
import com.tonedefapps.btw.ui.theme.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PairVehicleScreen(
    onVehiclePaired: () -> Unit,
    onSkip: () -> Unit,
    viewModel: PairVehicleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val pairingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return@rememberLauncherForActivityResult
            // API 33+ returns AssociationInfo; below that returns BluetoothDevice directly
            @Suppress("DEPRECATION")
            val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val assoc = data.getParcelableExtra(
                    CompanionDeviceManager.EXTRA_ASSOCIATION,
                    android.companion.AssociationInfo::class.java
                )
                assoc?.associatedDevice?.bluetoothDevice
            } else {
                data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
            }
            if (device != null) {
                viewModel.onDevicePaired(device)
                onVehiclePaired()
            }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .padding(horizontal = 32.dp, vertical = 56.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Wordmark()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "pair your vehicle",
                    style = MaterialTheme.typography.displaySmall,
                    color = Air
                )
                Text(
                    text = "btw watches for when your car's bluetooth disconnects. pair the audio system or hands-free kit.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Sky
                )
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Sky
                    )
                }
                if (uiState.pairedDeviceName != null) {
                    Text(
                        text = "paired: ${uiState.pairedDeviceName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Sand
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BtwButton(text = "scan for vehicle", onClick = ::startPairing)
                TextButton(onClick = onSkip) {
                    Text("skip for now", color = Sky, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

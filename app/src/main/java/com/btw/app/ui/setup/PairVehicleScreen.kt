package com.btw.app.ui.setup

import android.app.Activity
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.IntentSender
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
import com.btw.app.ui.onboarding.BtwButton
import com.btw.app.ui.onboarding.Wordmark
import com.btw.app.ui.theme.*

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
            val device = result.data
                ?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
            if (device != null) {
                viewModel.onDevicePaired(device)
                onVehiclePaired()
            }
        }
    }

    fun startPairing() {
        val cdm = context.getSystemService(CompanionDeviceManager::class.java)
        val request = AssociationRequest.Builder()
            .addDeviceFilter(BluetoothDeviceFilter.Builder().build())
            .setSingleDevice(false)
            .build()
        cdm.associate(request, object : CompanionDeviceManager.Callback() {
            override fun onDeviceFound(chooserLauncher: IntentSender) {
                pairingLauncher.launch(IntentSenderRequest.Builder(chooserLauncher).build())
            }
            override fun onFailure(error: CharSequence?) {
                viewModel.onPairingFailed()
            }
        }, null)
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

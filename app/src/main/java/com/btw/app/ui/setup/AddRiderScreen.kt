package com.btw.app.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.domain.model.RiderType
import com.btw.app.ui.onboarding.BtwButton
import com.btw.app.ui.onboarding.Wordmark
import com.btw.app.ui.theme.*

@Composable
fun AddRiderScreen(
    onRiderAdded: () -> Unit,
    onSkip: () -> Unit,
    viewModel: AddRiderViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RiderType.CHILD) }
    var emoji by remember { mutableStateOf("") }

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
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "who are you watching for?",
                    style = MaterialTheme.typography.displaySmall,
                    color = Air
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("name", color = Sky) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Sand,
                        unfocusedTextColor = Sand,
                        focusedBorderColor = Sky,
                        unfocusedBorderColor = Depth,
                        cursorColor = Sky
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RiderType.entries.forEach { type ->
                        val selected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    width = 1.dp,
                                    color = if (selected) Sky else Depth,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = if (selected) Depth else Ink,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedType = type }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.name.lowercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) Air else Sky
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    label = { Text("emoji (optional)", color = Sky) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Air,
                        unfocusedTextColor = Air,
                        focusedBorderColor = Sky,
                        unfocusedBorderColor = Depth,
                        cursorColor = Sky
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BtwButton(
                    text = "add ${if (name.isBlank()) "rider" else name}",
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addRider(name.trim(), selectedType, emoji)
                            onRiderAdded()
                        }
                    }
                )
                TextButton(onClick = onSkip) {
                    Text("skip for now", color = Sky, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

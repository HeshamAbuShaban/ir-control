package dev.training.ir_control.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import dev.training.ir_control.model.DevicePreset

@Composable
fun DeviceSelector(
        devices: List<DevicePreset>,
        selected: DevicePreset?,
        onSelect: (DevicePreset?) -> Unit,
        modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
                value = selected?.model ?: "Select Device",
                onValueChange = {},
                readOnly = true,
                label = { Text("Device") },
                trailingIcon = { IconButton(onClick = { expanded = !expanded }) { Text("▼") } },
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                enabled = devices.isNotEmpty()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            devices.forEach { device ->
                DropdownMenuItem(
                        text = { Text(device.model ?: "Unknown Device") },
                        onClick = {
                            onSelect(device)
                            expanded = false
                        }
                )
            }
        }
    }
}

package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.training.ir_control.model.DevicePreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSelector(
    devices: List<DevicePreset>,
    selected: DevicePreset?,
    onSelect: (DevicePreset?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = selected?.model ?: "Select Device",
            onValueChange = {},
            readOnly = true,
            label = { Text("Device") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            enabled = devices.isNotEmpty()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
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


package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.training.ir_control.model.DeviceEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedDevicesList(
        devices: List<DeviceEntity>,
        selectedDevice: DeviceEntity?,
        onDeviceSelected: (DeviceEntity) -> Unit,
        onDeviceDelete: (DeviceEntity) -> Unit,
        modifier: Modifier = Modifier
) {
    if (devices.isEmpty()) {
        Card(
                modifier = modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
            Text(
                    text = "No saved devices",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(devices, key = { it.id }) { device ->
            SavedDeviceItem(
                    device = device,
                    isSelected = selectedDevice?.id == device.id,
                    onSelected = { onDeviceSelected(device) },
                    onDelete = { onDeviceDelete(device) },
                    modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedDeviceItem(
        device: DeviceEntity,
        isSelected: Boolean,
        onSelected: () -> Unit,
        onDelete: () -> Unit,
        modifier: Modifier = Modifier
) {
    Card(
            onClick = onSelected,
            modifier = modifier,
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                    ),
            elevation =
                    CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = device.brandName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                )
                if (device.modelName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = device.modelName,
                            style = MaterialTheme.typography.bodyMedium,
                            color =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.7f
                                        )
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    }
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Text(
                        text = "✕",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

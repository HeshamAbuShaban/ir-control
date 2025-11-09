package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SaveDeviceDialog(
        deviceName: String,
        onDeviceNameChange: (String) -> Unit,
        onSave: () -> Unit,
        onDismiss: () -> Unit,
        isSaving: Boolean,
        modifier: Modifier = Modifier
) {
        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Save Device") },
                text = {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Text("Enter a name for this device preset:")
                                OutlinedTextField(
                                        value = deviceName,
                                        onValueChange = onDeviceNameChange,
                                        label = { Text("Brand/Device Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                )
                        }
                },
                confirmButton = {
                        TextButton(onClick = onSave, enabled = !isSaving) {
                                if (isSaving) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(if (isSaving) "Saving..." else "Save")
                        }
                },
                dismissButton = {
                        TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancel") }
                },
                modifier = modifier
        )
}

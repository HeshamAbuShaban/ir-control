package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.training.ir_control.model.IrCommand

@Composable
fun CommandsGrid(
        commands: List<IrCommand>,
        onClick: (IrCommand) -> Unit,
        enabled: Boolean,
        modifier: Modifier = Modifier
) {
    if (commands.isEmpty()) {
        Card(
                modifier = modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
            Text(
                    text = "No commands available",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        commands.chunked(3).forEach { rowCommands ->
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCommands.forEach { command ->
                    CommandButton(
                            command = command,
                            onClick = { onClick(command) },
                            enabled = enabled,
                            modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rowCommands.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CommandButton(
        command: IrCommand,
        onClick: () -> Unit,
        enabled: Boolean,
        modifier: Modifier = Modifier
) {
    Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier.fillMaxWidth().height(80.dp),
            colors =
                    ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
    ) {
        val icon = remember(command.label) { iconForLabel(command.label) }

        Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                    text = command.label,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2
            )
        }
    }
}

private fun iconForLabel(label: String): ImageVector? {
    val normalized = label.lowercase()
    return when {
        "power" in normalized || "on/off" in normalized -> Icons.Filled.PowerSettingsNew
        normalized.contains("volume") && (normalized.contains("+") || normalized.contains("up")) ->
                Icons.Filled.VolumeUp
        normalized.contains("volume") &&
                (normalized.contains("-") || normalized.contains("down")) -> Icons.Filled.VolumeDown
        "mute" in normalized -> Icons.Filled.VolumeMute
        "channel" in normalized || "input" in normalized -> Icons.Filled.Tv
        "fan" in normalized -> Icons.Filled.Toys
        "ac" in normalized || "cool" in normalized || "heat" in normalized -> Icons.Filled.AcUnit
        else -> null
    }
}

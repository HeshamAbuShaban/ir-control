package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Text(
                text = command.label,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
        )
    }
}

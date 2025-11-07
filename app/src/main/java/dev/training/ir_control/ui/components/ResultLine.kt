package dev.training.ir_control.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.training.ir_control.ir.IrResult

@Composable
fun ResultLine(
    result: IrResult?,
    modifier: Modifier = Modifier
) {
    if (result == null) return

    val (backgroundColor, contentColor, message) = when (result) {
        is IrResult.Ok -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "✓ ${result.info}"
        )
        is IrResult.Err.NoEmitter -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "✗ ${result.reason}"
        )
        is IrResult.Err.Invalid -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "✗ Invalid: ${result.reason}"
        )
        is IrResult.Err.Failure -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "✗ Error: ${result.cause.message ?: "Unknown error"}"
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
    }
}


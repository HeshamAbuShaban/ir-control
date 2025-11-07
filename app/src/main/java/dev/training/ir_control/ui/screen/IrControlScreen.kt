package dev.training.ir_control.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.training.ir_control.ui.components.BrandSelector
import dev.training.ir_control.ui.components.CommandsGrid
import dev.training.ir_control.ui.components.DeviceSelector
import dev.training.ir_control.ui.components.ResultLine
import dev.training.ir_control.ui.vm.IrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrControlScreen(
    viewModel: IrViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.init()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IR Control") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // IR Status Card
            IrStatusCard(
                hasIr = state.hasIr,
                isLoading = state.isLoading
            )

            // Brand Selector
            BrandSelector(
                brands = state.brands,
                selected = state.selectedBrand,
                onSelect = viewModel::selectBrand
            )

            // Device Selector (only show if brand has multiple devices)
            if (state.selectedBrand?.devices?.size ?: 0 > 1) {
                DeviceSelector(
                    devices = state.selectedBrand?.devices.orEmpty(),
                    selected = state.selectedDevice,
                    onSelect = viewModel::selectDevice
                )
            }

            // Commands Grid
            Text(
                text = "Commands",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            CommandsGrid(
                commands = state.selectedDevice?.commands.orEmpty(),
                onClick = viewModel::send,
                enabled = state.hasIr == true
            )

            Spacer(modifier = Modifier.weight(1f))

            // Result Line
            ResultLine(result = state.lastResult)
        }
    }
}

@Composable
private fun IrStatusCard(
    hasIr: Boolean?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.surfaceVariant
                hasIr == true -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "IR Emitter Status",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        isLoading -> "Checking..."
                        hasIr == true -> "✓ Available"
                        hasIr == false -> "✗ Not Available"
                        else -> "Unknown"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isLoading -> MaterialTheme.colorScheme.onSurfaceVariant
                        hasIr == true -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
            if (hasIr == true) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else if (hasIr == false) {
                Text(
                    text = "✗",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}


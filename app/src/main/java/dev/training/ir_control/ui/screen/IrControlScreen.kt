package dev.training.ir_control.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import dev.training.ir_control.ui.components.SaveDeviceDialog
import dev.training.ir_control.ui.components.SavedDevicesList
import dev.training.ir_control.ui.vm.IrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrControlScreen(viewModel: IrViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { viewModel.init() }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("IR Control") },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor =
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                        actions = {
                            if (selectedTabIndex == 0 && state.selectedDevice != null) {
                                IconButton(onClick = { viewModel.showSaveDialog() }) {
                                    Text("💾", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }
                )
            }
    ) { paddingValues ->
        Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
            // IR Status Card
            IrStatusCard(
                    hasIr = state.hasIr,
                    isLoading = state.isLoading,
                    modifier = Modifier.padding(16.dp)
            )

            // Tabs
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Presets") }
                )
                Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Saved Devices") }
                )
            }

            // Tab Content
            when (selectedTabIndex) {
                0 ->
                        PresetsTab(
                                state = state,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize().weight(1f)
                        )
                1 ->
                        SavedDevicesTab(
                                state = state,
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize().weight(1f)
                        )
            }

            // Result Line
            ResultLine(result = state.lastResult, modifier = Modifier.padding(16.dp))
        }

        // Save Dialog
        if (state.showSaveDialog) {
            SaveDeviceDialog(
                    deviceName = state.saveDeviceName,
                    onDeviceNameChange = viewModel::updateSaveDeviceName,
                    onSave = viewModel::saveCurrentDevice,
                    onDismiss = viewModel::hideSaveDialog
            )
        }
    }
}

@Composable
private fun PresetsTab(
        state: dev.training.ir_control.ui.vm.IrViewModel.UiState,
        viewModel: IrViewModel,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
    }
}

@Composable
private fun SavedDevicesTab(
        state: dev.training.ir_control.ui.vm.IrViewModel.UiState,
        viewModel: IrViewModel,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
                text = "Saved Devices",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
        )

        // Saved Devices List
        SavedDevicesList(
                devices = state.savedDevices,
                selectedDevice = state.selectedSavedDevice,
                onDeviceSelected = viewModel::selectSavedDevice,
                onDeviceDelete = viewModel::deleteSavedDevice,
                modifier = Modifier.weight(1f)
        )

        // Commands for selected saved device
        if (state.selectedSavedDevice != null && state.savedDeviceCommands.isNotEmpty()) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                    text = "Commands for ${state.selectedSavedDevice.brandName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )
            CommandsGrid(
                    commands = state.savedDeviceCommands,
                    onClick = viewModel::send,
                    enabled = state.hasIr == true
            )
        }
    }
}

@Composable
private fun IrStatusCard(hasIr: Boolean?, isLoading: Boolean, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    when {
                                        isLoading -> MaterialTheme.colorScheme.surfaceVariant
                                        hasIr == true -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.errorContainer
                                    }
                    )
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                        text =
                                when {
                                    isLoading -> "Checking..."
                                    hasIr == true -> "✓ Available"
                                    hasIr == false -> "✗ Not Available"
                                    else -> "Unknown"
                                },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color =
                                when {
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

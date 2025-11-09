package dev.training.ir_control.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.training.ir_control.ui.vm.IrViewModel.SaveSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrControlScreen(viewModel: IrViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.init() }

    LaunchedEffect(state.snackbarMessage) {
        val message = state.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 2) {
            viewModel.fetchBrands()
        }
    }

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
                            if (selectedTabIndex == 0 && state.selectedPresetDevice != null) {
                                IconButton(
                                        onClick = {
                                            viewModel.showSaveDialog(
                                                    SaveSource.PRESET,
                                                    state.selectedPresetBrand?.name ?: ""
                                            )
                                        }
                                ) { Icon(Icons.Filled.Save, contentDescription = "حفظ الجهاز") }
                            }
                        }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
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
                Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Online") }
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
                2 ->
                        OnlineTab(
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
                    onDismiss = viewModel::hideSaveDialog,
                    isSaving = state.isSaving
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
                brands = state.presetBrands,
                selected = state.selectedPresetBrand,
                onSelect = viewModel::selectPresetBrand
        )

        // Device Selector (only show if brand has multiple devices)
        if (state.selectedPresetBrand?.devices?.size ?: 0 > 1) {
            DeviceSelector(
                    devices = state.selectedPresetBrand?.devices.orEmpty(),
                    selected = state.selectedPresetDevice,
                    onSelect = viewModel::selectPresetDevice
            )
        }

        // Commands Grid
        Text(
                text = "Commands",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
        )
        CommandsGrid(
                commands = state.selectedPresetDevice?.commands.orEmpty(),
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
private fun OnlineTab(
        state: dev.training.ir_control.ui.vm.IrViewModel.UiState,
        viewModel: IrViewModel,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RemoteDropdown(
                label = "Brand",
                items = state.onlineBrands,
                selectedItem = state.selectedOnlineBrand,
                isLoading = state.isOnlineLoading && state.onlineBrands.isEmpty(),
                onSelect = viewModel::fetchDevices,
                itemLabel = { it.name }
        )

        RemoteDropdown(
                label = "Device",
                items = state.onlineDevices,
                selectedItem = state.selectedOnlineDevice,
                isLoading = state.isOnlineLoading && state.onlineDevices.isEmpty(),
                onSelect = viewModel::fetchCommands,
                itemLabel = { it.name ?: "Unnamed" }
        )

        if (state.isOnlineLoading && state.onlineCommands.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        CommandsGrid(
                commands = state.onlineCommands,
                onClick = viewModel::send,
                enabled = state.hasIr == true
        )

        ElevatedButton(
                onClick = {
                    viewModel.showSaveDialog(
                            SaveSource.ONLINE,
                            state.selectedOnlineBrand?.name
                                    ?: state.selectedOnlineDevice?.name ?: ""
                    )
                },
                enabled = state.onlineCommands.isNotEmpty() && !state.isSaving
        ) {
            Icon(Icons.Filled.CloudDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save locally")
        }

        if (state.isSaving) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Saving...")
            }
        }
    }
}

@Composable
private fun <T> RemoteDropdown(
        label: String,
        items: List<T>,
        selectedItem: T?,
        isLoading: Boolean,
        onSelect: (T) -> Unit,
        itemLabel: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val textFieldModifier =
                if (!isLoading && items.isNotEmpty()) {
                    Modifier.fillMaxWidth().clickable { expanded = !expanded }
                } else {
                    Modifier.fillMaxWidth()
                }

        Box {
            OutlinedTextField(
                    value = selectedItem?.let(itemLabel)
                                    ?: if (isLoading) "Loading..." else "Select $label",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(label) },
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                    },
                    enabled = items.isNotEmpty() && !isLoading,
                    modifier = textFieldModifier,
                    singleLine = true
            )

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(
                            text = { Text(itemLabel(item)) },
                            onClick = {
                                onSelect(item)
                                expanded = false
                            }
                    )
                }
            }
        }

        LaunchedEffect(items, isLoading) { expanded = false }
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

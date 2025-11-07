package dev.training.ir_control.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.training.ir_control.data.PresetRepository
import dev.training.ir_control.data.SamsungPresets
import dev.training.ir_control.ir.IrController
import dev.training.ir_control.ir.IrResult
import dev.training.ir_control.model.BrandPreset
import dev.training.ir_control.model.DeviceEntity
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class IrViewModel
@Inject
constructor(private val ir: IrController, private val repository: PresetRepository) : ViewModel() {

    data class UiState(
            val hasIr: Boolean? = null,
            val isLoading: Boolean = false,
            val brands: List<BrandPreset> = emptyList(),
            val selectedBrand: BrandPreset? = null,
            val selectedDevice: DevicePreset? = null,
            val savedDevices: List<DeviceEntity> = emptyList(),
            val selectedSavedDevice: DeviceEntity? = null,
            val savedDeviceCommands: List<IrCommand> = emptyList(),
            val lastResult: IrResult? = null,
            val lastResultMessage: String? = null,
            val showSaveDialog: Boolean = false,
            val saveDeviceName: String = ""
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        loadPresets()
        loadSavedDevices()
    }

    fun init() {
        _state.update { it.copy(hasIr = ir.hasIrEmitter()) }
    }

    private fun loadPresets() {
        _state.update { it.copy(isLoading = true) }
        val brands = SamsungPresets.getAllBrands()
        _state.update {
            it.copy(
                    brands = brands,
                    selectedBrand = brands.firstOrNull(),
                    selectedDevice = brands.firstOrNull()?.devices?.firstOrNull(),
                    isLoading = false
            )
        }
    }

    private fun loadSavedDevices() {
        viewModelScope.launch {
            repository.getAllDevices().collect { devices ->
                _state.update { it.copy(savedDevices = devices) }
            }
        }
    }

    fun selectBrand(brand: BrandPreset) {
        _state.update {
            it.copy(selectedBrand = brand, selectedDevice = brand.devices.firstOrNull())
        }
    }

    fun selectDevice(device: DevicePreset?) {
        _state.update { it.copy(selectedDevice = device) }
    }

    fun selectSavedDevice(device: DeviceEntity?) {
        _state.update { it.copy(selectedSavedDevice = device) }
        if (device != null) {
            loadCommandsForSavedDevice(device.id)
        } else {
            _state.update { it.copy(savedDeviceCommands = emptyList()) }
        }
    }

    private fun loadCommandsForSavedDevice(deviceId: Long) {
        viewModelScope.launch {
            repository.getCommandsForDevice(deviceId).collect { commandEntities ->
                val commands =
                        commandEntities.map { cmdEntity ->
                            val payload =
                                    when (dev.training.ir_control.model.Protocol.valueOf(
                                                    cmdEntity.protocol
                                            )
                                    ) {
                                        dev.training.ir_control.model.Protocol.NEC ->
                                                Payload.Nec(
                                                        address = cmdEntity.address,
                                                        command = cmdEntity.command
                                                )
                                        dev.training.ir_control.model.Protocol.SIRC ->
                                                TODO("SIRC not implemented")
                                        dev.training.ir_control.model.Protocol.RC5 ->
                                                TODO("RC5 not implemented")
                                    }
                            IrCommand(label = cmdEntity.label, payload = payload)
                        }
                _state.update { it.copy(savedDeviceCommands = commands) }
            }
        }
    }

    fun showSaveDialog() {
        _state.update {
            it.copy(showSaveDialog = true, saveDeviceName = it.selectedBrand?.name ?: "")
        }
    }

    fun hideSaveDialog() {
        _state.update { it.copy(showSaveDialog = false, saveDeviceName = "") }
    }

    fun updateSaveDeviceName(name: String) {
        _state.update { it.copy(saveDeviceName = name) }
    }

    fun saveCurrentDevice() {
        val brandName = _state.value.saveDeviceName.trim()
        val device = _state.value.selectedDevice

        if (brandName.isEmpty() || device == null) {
            _state.update {
                it.copy(
                        lastResult = IrResult.Err.Invalid("Brand name and device are required"),
                        lastResultMessage = "Please enter a brand name and select a device"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                repository.saveDevice(brandName, device)
                _state.update {
                    it.copy(
                            showSaveDialog = false,
                            saveDeviceName = "",
                            lastResult = IrResult.Ok("Device saved successfully"),
                            lastResultMessage = "Device saved successfully"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                            lastResult = IrResult.Err.Failure(e),
                            lastResultMessage = "Error saving device: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteSavedDevice(device: DeviceEntity) {
        viewModelScope.launch {
            try {
                repository.deleteDevice(device.id)
                if (_state.value.selectedSavedDevice?.id == device.id) {
                    _state.update {
                        it.copy(selectedSavedDevice = null, savedDeviceCommands = emptyList())
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                            lastResult = IrResult.Err.Failure(e),
                            lastResultMessage = "Error deleting device: ${e.message}"
                    )
                }
            }
        }
    }

    fun send(cmd: IrCommand) {
        if (_state.value.hasIr != true) {
            _state.update {
                it.copy(
                        lastResult = IrResult.Err.NoEmitter("IR emitter not available"),
                        lastResultMessage = "IR emitter not available"
                )
            }
            return
        }

        val result =
                when (val payload = cmd.payload) {
                    is Payload.Nec -> ir.transmitNec(payload.address, payload.command)
                }

        val message =
                when (result) {
                    is IrResult.Ok -> result.info
                    is IrResult.Err.NoEmitter -> "No IR emitter: ${result.reason}"
                    is IrResult.Err.Invalid -> "Invalid: ${result.reason}"
                    is IrResult.Err.Failure -> "Error: ${result.cause.message}"
                }

        _state.update { it.copy(lastResult = result, lastResultMessage = message) }
    }

    fun clearLastResult() {
        _state.update { it.copy(lastResult = null, lastResultMessage = null) }
    }
}

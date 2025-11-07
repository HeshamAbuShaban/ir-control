package dev.training.ir_control.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.training.ir_control.data.SamsungPresets
import dev.training.ir_control.ir.IrController
import dev.training.ir_control.ir.IrResult
import dev.training.ir_control.model.BrandPreset
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IrViewModel @Inject constructor(
    private val ir: IrController
) : ViewModel() {

    data class UiState(
        val hasIr: Boolean? = null,
        val isLoading: Boolean = false,
        val brands: List<BrandPreset> = emptyList(),
        val selectedBrand: BrandPreset? = null,
        val selectedDevice: DevicePreset? = null,
        val lastResult: IrResult? = null,
        val lastResultMessage: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        loadPresets()
    }

    fun init() {
        _state.update {
            it.copy(hasIr = ir.hasIrEmitter())
        }
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

    fun selectBrand(brand: BrandPreset) {
        _state.update {
            it.copy(
                selectedBrand = brand,
                selectedDevice = brand.devices.firstOrNull()
            )
        }
    }

    fun selectDevice(device: DevicePreset?) {
        _state.update { it.copy(selectedDevice = device) }
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

        val result = when (val payload = cmd.payload) {
            is Payload.Nec -> ir.transmitNec(payload.address, payload.command)
        }

        val message = when (result) {
            is IrResult.Ok -> result.info
            is IrResult.Err.NoEmitter -> "No IR emitter: ${result.reason}"
            is IrResult.Err.Invalid -> "Invalid: ${result.reason}"
            is IrResult.Err.Failure -> "Error: ${result.cause.message}"
        }

        _state.update {
            it.copy(
                lastResult = result,
                lastResultMessage = message
            )
        }
    }

    fun clearLastResult() {
        _state.update {
            it.copy(
                lastResult = null,
                lastResultMessage = null
            )
        }
    }
}

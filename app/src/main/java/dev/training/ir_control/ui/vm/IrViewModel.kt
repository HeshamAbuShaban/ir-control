package dev.training.ir_control.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.training.ir_control.data.PresetRepository
import dev.training.ir_control.data.remote.RemoteBrand
import dev.training.ir_control.data.remote.RemoteDevice
import dev.training.ir_control.ir.IrController
import dev.training.ir_control.ir.IrResult
import dev.training.ir_control.model.BrandPreset
import dev.training.ir_control.model.DeviceEntity
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class IrViewModel
@Inject
constructor(private val ir: IrController, private val repository: PresetRepository) : ViewModel() {

    enum class SaveSource {
        PRESET,
        ONLINE
    }

    data class UiState(
            val hasIr: Boolean? = null,
            val isLoading: Boolean = false,
            val presetBrands: List<BrandPreset> = emptyList(),
            val selectedPresetBrand: BrandPreset? = null,
            val selectedPresetDevice: DevicePreset? = null,
            val savedDevices: List<DeviceEntity> = emptyList(),
            val selectedSavedDevice: DeviceEntity? = null,
            val savedDeviceCommands: List<IrCommand> = emptyList(),
            val onlineBrands: List<RemoteBrand> = emptyList(),
            val selectedOnlineBrand: RemoteBrand? = null,
            val onlineDevices: List<RemoteDevice> = emptyList(),
            val selectedOnlineDevice: RemoteDevice? = null,
            val onlineCommands: List<IrCommand> = emptyList(),
            val isOnlineLoading: Boolean = false,
            val snackbarMessage: String? = null,
            val showSaveDialog: Boolean = false,
            val saveDeviceName: String = "",
            val saveSource: SaveSource? = null,
            val isSaving: Boolean = false,
            val lastResult: IrResult? = null,
            val lastResultMessage: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var savedCommandsJob: Job? = null

    init {
        loadPresets()
        observeSavedDevices()
    }

    fun init() {
        _state.update { it.copy(hasIr = ir.hasIrEmitter()) }
    }

    private fun loadPresets() {
        _state.update { it.copy(isLoading = true) }
        val brands = repository.getDefaultPresets()
        _state.update {
            it.copy(
                    presetBrands = brands,
                    selectedPresetBrand = brands.firstOrNull(),
                    selectedPresetDevice = brands.firstOrNull()?.devices?.firstOrNull(),
                    isLoading = false
            )
        }
    }

    private fun observeSavedDevices() {
        viewModelScope.launch {
            repository.observeSavedDevices().collect { devices ->
                _state.update { it.copy(savedDevices = devices) }
            }
        }
    }

    fun selectPresetBrand(brand: BrandPreset) {
        _state.update {
            it.copy(selectedPresetBrand = brand, selectedPresetDevice = brand.devices.firstOrNull())
        }
    }

    fun selectPresetDevice(device: DevicePreset?) {
        _state.update { it.copy(selectedPresetDevice = device) }
    }

    fun selectSavedDevice(device: DeviceEntity?) {
        _state.update { it.copy(selectedSavedDevice = device) }
        savedCommandsJob?.cancel()
        if (device == null) {
            _state.update { it.copy(savedDeviceCommands = emptyList()) }
            return
        }
        savedCommandsJob =
                viewModelScope.launch {
                    repository.observeCommands(device.id).collect { commands ->
                        _state.update { it.copy(savedDeviceCommands = commands) }
                    }
                }
    }

    fun fetchBrands(force: Boolean = false) {
        if (!force && _state.value.onlineBrands.isNotEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isOnlineLoading = true, snackbarMessage = null) }
            runCatching { repository.fetchRemoteBrands() }
                    .onSuccess { brands ->
                        _state.update {
                            it.copy(
                                    onlineBrands = brands,
                                    isOnlineLoading = false,
                                    selectedOnlineBrand = brands.firstOrNull(),
                                    onlineDevices = emptyList(),
                                    selectedOnlineDevice = null,
                                    onlineCommands = emptyList()
                            )
                        }
                        brands.firstOrNull()?.let { fetchDevices(it) }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                    isOnlineLoading = false,
                                    snackbarMessage = error.message ?: "فشل تحميل العلامات"
                            )
                        }
                    }
        }
    }

    fun fetchDevices(brand: RemoteBrand) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                        isOnlineLoading = true,
                        selectedOnlineBrand = brand,
                        onlineDevices = emptyList(),
                        selectedOnlineDevice = null,
                        onlineCommands = emptyList(),
                        snackbarMessage = null
                )
            }
            runCatching { repository.fetchRemoteDevices(brand.id) }
                    .onSuccess { devices ->
                        _state.update {
                            it.copy(
                                    isOnlineLoading = false,
                                    onlineDevices = devices,
                                    selectedOnlineDevice = devices.firstOrNull(),
                                    onlineCommands = emptyList()
                            )
                        }
                        devices.firstOrNull()?.let { fetchCommands(it) }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                    isOnlineLoading = false,
                                    snackbarMessage = error.message ?: "فشل تحميل الأجهزة"
                            )
                        }
                    }
        }
    }

    fun fetchCommands(device: RemoteDevice) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                        isOnlineLoading = true,
                        selectedOnlineDevice = device,
                        onlineCommands = emptyList(),
                        snackbarMessage = null
                )
            }
            runCatching { repository.fetchRemoteCommands(device.id) }
                    .onSuccess { commands ->
                        _state.update {
                            it.copy(
                                    isOnlineLoading = false,
                                    onlineCommands = commands.map { it.toIrCommand() }
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                    isOnlineLoading = false,
                                    snackbarMessage = error.message ?: "فشل تحميل الأوامر"
                            )
                        }
                    }
        }
    }

    fun showSaveDialog(source: SaveSource, suggestedName: String) {
        _state.update {
            it.copy(showSaveDialog = true, saveDeviceName = suggestedName, saveSource = source)
        }
    }

    fun hideSaveDialog() {
        _state.update { it.copy(showSaveDialog = false, saveDeviceName = "", saveSource = null) }
    }

    fun updateSaveDeviceName(name: String) {
        _state.update { it.copy(saveDeviceName = name) }
    }

    fun saveCurrentDevice() {
        val state = _state.value
        if (state.isSaving) return
        val brandName = state.saveDeviceName.trim()
        val source = state.saveSource

        if (brandName.isEmpty() || source == null) {
            _state.update {
                it.copy(
                        lastResult = IrResult.Err.Invalid("الاسم غير صالح"),
                        lastResultMessage = "الرجاء إدخال اسم جهاز صحيح"
                )
            }
            return
        }

        when (source) {
            SaveSource.PRESET -> savePresetSelection(brandName)
            SaveSource.ONLINE -> saveOnlineSelection(brandName)
        }
    }

    private fun savePresetSelection(brandName: String) {
        val state = _state.value
        val brand = state.selectedPresetBrand
        val device = state.selectedPresetDevice
        if (brand == null || device == null) {
            _state.update {
                it.copy(
                        lastResult = IrResult.Err.Invalid("لا يوجد جهاز محدد"),
                        lastResultMessage = "اختر جهازًا لحفظه"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching {
                repository.saveDevice(
                        brandName = brandName,
                        protocol = brand.protocol,
                        frequencyHz = brand.defaultFreqHz,
                        devicePreset = device
                )
            }
                    .onSuccess {
                        _state.update {
                            it.copy(
                                    isSaving = false,
                                    showSaveDialog = false,
                                    saveDeviceName = "",
                                    saveSource = null,
                                    lastResult = IrResult.Ok("تم الحفظ"),
                                    lastResultMessage = "تم حفظ الجهاز بنجاح"
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                    isSaving = false,
                                    lastResult = IrResult.Err.Failure(error),
                                    lastResultMessage = error.message ?: "خطأ أثناء الحفظ"
                            )
                        }
                    }
        }
    }

    private fun saveOnlineSelection(brandName: String) {
        val state = _state.value
        val brand = state.selectedOnlineBrand
        val device = state.selectedOnlineDevice
        val commands = state.onlineCommands
        if (brand == null || device == null || commands.isEmpty()) {
            _state.update {
                it.copy(
                        lastResult = IrResult.Err.Invalid("بيانات غير مكتملة"),
                        lastResultMessage = "تأكد من اختيار جهاز وتحميل أوامره"
                )
            }
            return
        }

        val devicePreset =
                DevicePreset(
                        model = device.name,
                        commands = commands,
                        protocolOverride = device.protocol,
                        frequencyOverrideHz = device.frequencyHz
                )

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            runCatching {
                repository.saveDevice(
                        brandName = brandName,
                        protocol = device.protocol,
                        frequencyHz = device.frequencyHz,
                        devicePreset = devicePreset
                )
            }
                    .onSuccess {
                        _state.update {
                            it.copy(
                                    isSaving = false,
                                    showSaveDialog = false,
                                    saveDeviceName = "",
                                    saveSource = null,
                                    lastResult = IrResult.Ok("تم الحفظ"),
                                    lastResultMessage = "تم حفظ الجهاز المستورد"
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                    isSaving = false,
                                    lastResult = IrResult.Err.Failure(error),
                                    lastResultMessage = error.message ?: "تعذر حفظ الجهاز"
                            )
                        }
                    }
        }
    }

    fun deleteSavedDevice(device: DeviceEntity) {
        viewModelScope.launch {
            runCatching { repository.deleteDevice(device.id) }
                    .onSuccess {
                        if (_state.value.selectedSavedDevice?.id == device.id) {
                            _state.update {
                                it.copy(
                                        selectedSavedDevice = null,
                                        savedDeviceCommands = emptyList()
                                )
                            }
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                    lastResult = IrResult.Err.Failure(error),
                                    lastResultMessage = error.message ?: "تعذر حذف الجهاز"
                            )
                        }
                    }
        }
    }

    fun send(command: IrCommand) {
        if (_state.value.hasIr != true) {
            _state.update {
                it.copy(
                        lastResult = IrResult.Err.NoEmitter("لا يوجد باعث IR"),
                        lastResultMessage = "جهازك لا يحتوي على باعث IR"
                )
            }
            return
        }

        val result = ir.transmit(command.payload)
        val message =
                when (result) {
                    is IrResult.Ok -> result.info
                    is IrResult.Err.NoEmitter -> "لا يوجد باعث: ${result.reason}"
                    is IrResult.Err.Invalid -> "إشارة غير صحيحة: ${result.reason}"
                    is IrResult.Err.Failure -> "خطأ: ${result.cause.message}"
                }
        _state.update { it.copy(lastResult = result, lastResultMessage = message) }
    }

    fun clearLastResult() {
        _state.update { it.copy(lastResult = null, lastResultMessage = null) }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbarMessage = null) }
    }
}

package dev.training.ir_control.model

/**
 * Represents a brand preset with protocol and device configurations.
 */
data class BrandPreset(
    val name: String,
    val protocol: Protocol,
    val defaultFreqHz: Int,
    val devices: List<DevicePreset> = emptyList()
)


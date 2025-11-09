package dev.training.ir_control.model

/** Represents a device preset with its commands. */
data class DevicePreset(
        val model: String?,
        val commands: List<IrCommand>,
        val protocolOverride: Protocol? = null,
        val frequencyOverrideHz: Int? = null
)

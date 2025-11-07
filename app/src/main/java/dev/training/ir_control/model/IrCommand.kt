package dev.training.ir_control.model

/**
 * Represents an IR command with a label and payload.
 */
data class IrCommand(
    val label: String,
    val payload: Payload
)


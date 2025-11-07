package dev.training.ir_control.model

/**
 * Sealed interface for different IR protocol payloads.
 */
sealed interface Payload {
    /**
     * NEC protocol payload with address and command.
     */
    data class Nec(val address: Int, val command: Int) : Payload
    // Future protocols can be added here:
    // data class Sirc(val command: Int) : Payload
    // data class Rc5(val address: Int, val command: Int) : Payload
}


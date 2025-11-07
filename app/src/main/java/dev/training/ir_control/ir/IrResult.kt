package dev.training.ir_control.ir

sealed interface IrResult {
    data class Ok(val info: String = "OK") : IrResult
    sealed interface Err : IrResult {
        data class NoEmitter(val reason: String = "No IR") : Err
        data class Invalid(val reason: String) : Err
        data class Failure(val cause: Throwable) : Err
    }
}
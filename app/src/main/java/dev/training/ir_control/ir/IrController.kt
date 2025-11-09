package dev.training.ir_control.ir

interface IrController {
    fun hasIrEmitter(): Boolean
    fun transmitRaw(frequencyHz: Int, patternMicros: IntArray): IrResult
    fun transmit(payload: dev.training.ir_control.model.Payload): IrResult
}

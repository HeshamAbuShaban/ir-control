package dev.training.ir_control.ir

interface IrController {
    fun hasIrEmitter(): Boolean
    fun transmitRaw(frequencyHz: Int, patternMicros: IntArray): IrResult
    fun transmitNec(address: Int, command: Int): IrResult // convenience
}

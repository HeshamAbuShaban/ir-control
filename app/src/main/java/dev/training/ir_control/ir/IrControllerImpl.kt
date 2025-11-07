package dev.training.ir_control.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import dev.training.ir_control.ir.protocol.NecEncoder

class IrControllerImpl(
    context: Context
) : IrController {

    private val ir: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    override fun hasIrEmitter(): Boolean = ir?.hasIrEmitter() == true

    override fun transmitRaw(frequencyHz: Int, patternMicros: IntArray): IrResult {
        if (!hasIrEmitter()) {
            return IrResult.Err.NoEmitter("IR emitter not available on this device")
        }

        val service = ir ?: return IrResult.Err.NoEmitter("IR service not available")

        if (patternMicros.isEmpty()) {
            return IrResult.Err.Invalid("Pattern array cannot be empty")
        }

        if (frequencyHz <= 0) {
            return IrResult.Err.Invalid("Frequency must be positive")
        }

        return try {
            service.transmit(frequencyHz, patternMicros)
            IrResult.Ok("Transmitted successfully")
        } catch (e: Exception) {
            IrResult.Err.Failure(e)
        }
    }

    override fun transmitNec(address: Int, command: Int): IrResult {
        if (!hasIrEmitter()) {
            return IrResult.Err.NoEmitter("IR emitter not available on this device")
        }

        val pattern = NecEncoder.encode(address, command)
        return transmitRaw(NecEncoder.DEFAULT_FREQUENCY_HZ, pattern)
    }
}

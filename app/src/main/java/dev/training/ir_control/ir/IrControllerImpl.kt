package dev.training.ir_control.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import dev.training.ir_control.ir.protocol.NecEncoder
import dev.training.ir_control.ir.protocol.PanasonicEncoder
import dev.training.ir_control.ir.protocol.Rc5Encoder
import dev.training.ir_control.ir.protocol.Rc6Encoder
import dev.training.ir_control.ir.protocol.SharpEncoder
import dev.training.ir_control.ir.protocol.SircEncoder
import dev.training.ir_control.model.Payload

class IrControllerImpl(context: Context) : IrController {

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

        if (patternMicros.any { it <= 0 }) {
            return IrResult.Err.Invalid("Pattern values must be positive durations in microseconds")
        }

        if (frequencyHz !in 1000..200_000) {
            return IrResult.Err.Invalid("Frequency must be between 1kHz and 200kHz")
        }

        return try {
            service.transmit(frequencyHz, patternMicros)
            IrResult.Ok("Transmitted successfully")
        } catch (e: Exception) {
            IrResult.Err.Failure(e)
        }
    }

    override fun transmit(payload: Payload): IrResult {
        val (frequency, pattern) =
                when (payload) {
                    is Payload.Nec ->
                            NecEncoder.DEFAULT_FREQUENCY_HZ to
                                    NecEncoder.encode(payload.address, payload.command)
                    is Payload.Sirc ->
                            SircEncoder.DEFAULT_FREQUENCY_HZ to
                                    SircEncoder.encode(
                                            command = payload.command,
                                            device = payload.device,
                                            bits = payload.bits
                                    )
                    is Payload.Rc5 ->
                            Rc5Encoder.DEFAULT_FREQUENCY_HZ to
                                    Rc5Encoder.encode(
                                            address = payload.address,
                                            command = payload.command,
                                            toggle = payload.toggle
                                    )
                    is Payload.Rc6 ->
                            Rc6Encoder.DEFAULT_FREQUENCY_HZ to
                                    Rc6Encoder.encode(
                                            mode = payload.mode,
                                            address = payload.address,
                                            command = payload.command,
                                            toggle = payload.toggle,
                                            bits = payload.bits
                                    )
                    is Payload.Panasonic ->
                            PanasonicEncoder.DEFAULT_FREQUENCY_HZ to
                                    PanasonicEncoder.encode(
                                            vendor = payload.vendor,
                                            address = payload.address,
                                            command = payload.command
                                    )
                    is Payload.Sharp ->
                            SharpEncoder.DEFAULT_FREQUENCY_HZ to
                                    SharpEncoder.encode(
                                            address = payload.address,
                                            command = payload.command,
                                            repeat = payload.repeat
                                    )
                    is Payload.Raw -> payload.frequencyHz to payload.patternMicros.toIntArray()
                }

        return transmitRaw(frequency, pattern)
    }
}

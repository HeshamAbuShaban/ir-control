package dev.training.ir_control.ir.protocol

/** Sony SIRC encoder (supports 12/15/20-bit variants). */
object SircEncoder {
    const val DEFAULT_FREQUENCY_HZ = 40000

    private const val LEADER_PULSE = 2400
    private const val LEADER_SPACE = 600
    private const val BIT_SPACE = 600
    private const val BIT_ONE_PULSE = 1200
    private const val BIT_ZERO_PULSE = 600

    fun encode(command: Int, device: Int, bits: Int = 12): IntArray {
        require(bits in setOf(12, 15, 20)) { "SIRC supports 12, 15 or 20 bit payloads" }

        val payloadValue = buildPayloadValue(command, device, bits)
        val builder = PatternBuilder()
        builder.mark(LEADER_PULSE)
        builder.space(LEADER_SPACE)

        repeat(bits) { bitIndex ->
            val bit = (payloadValue shr bitIndex) and 0x1 // LSB first
            if (bit == 1) {
                builder.mark(BIT_ONE_PULSE)
            } else {
                builder.mark(BIT_ZERO_PULSE)
            }
            builder.space(BIT_SPACE)
        }

        builder.ensureTrailingSpace(BIT_SPACE)

        return builder.build()
    }

    private fun buildPayloadValue(command: Int, device: Int, bits: Int): Int {
        val commandBits = 7
        val deviceBits = bits - commandBits
        val commandMask = (1 shl commandBits) - 1
        val deviceMask = if (deviceBits >= 32) -1 else (1 shl deviceBits) - 1
        val normalizedCommand = command and commandMask
        val normalizedDevice = device and deviceMask
        return normalizedCommand or (normalizedDevice shl commandBits)
    }
}

package dev.training.ir_control.ir.protocol

/** Sharp (15-bit) encoder approximation. */
object SharpEncoder {
    const val DEFAULT_FREQUENCY_HZ = 38000

    private const val LEADER_PULSE = 3200
    private const val LEADER_SPACE = 960
    private const val BIT_MARK = 320
    private const val ZERO_SPACE = 960
    private const val ONE_SPACE = 1920

    fun encode(address: Int, command: Int, repeat: Boolean = false): IntArray {
        val payload = ((address and 0x7F) shl 8) or (command and 0xFF)
        val bits = 15

        val builder = PatternBuilder()
        builder.mark(LEADER_PULSE)
        builder.space(LEADER_SPACE)

        repeat(bits) { idx ->
            val bit = (payload shr idx) and 0x1
            builder.mark(BIT_MARK)
            if (bit == 1) {
                builder.space(ONE_SPACE)
            } else {
                builder.space(ZERO_SPACE)
            }
        }

        if (repeat) {
            builder.space(LEADER_SPACE)
            builder.mark(LEADER_PULSE)
            builder.space(LEADER_SPACE)
        }

        builder.ensureTrailingSpace(ZERO_SPACE)

        return builder.build()
    }
}

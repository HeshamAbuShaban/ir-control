package dev.training.ir_control.ir.protocol

/** Panasonic (Kaseikyo) encoder (48-bit frame). */
object PanasonicEncoder {
    const val DEFAULT_FREQUENCY_HZ = 37000

    private const val LEADER_PULSE = 3504
    private const val LEADER_SPACE = 1752
    private const val BIT_MARK = 438
    private const val ZERO_SPACE = 438
    private const val ONE_SPACE = 1314
    private const val TRAILER_MARK = 438

    fun encode(vendor: Int, address: Int, command: Int): IntArray {
        val frame: Long =
                ((vendor and 0xFFFF).toLong() shl 32) or
                        ((address and 0xFFFF).toLong() shl 16) or
                        (command and 0xFFFF).toLong()

        val builder = PatternBuilder()
        builder.mark(LEADER_PULSE)
        builder.space(LEADER_SPACE)

        for (i in 47 downTo 0) {
            val bit = ((frame shr i) and 0x1) == 1L
            builder.mark(BIT_MARK)
            if (bit) {
                builder.space(ONE_SPACE)
            } else {
                builder.space(ZERO_SPACE)
            }
        }

        builder.mark(TRAILER_MARK)
        builder.ensureTrailingSpace(ZERO_SPACE)

        return builder.build()
    }
}

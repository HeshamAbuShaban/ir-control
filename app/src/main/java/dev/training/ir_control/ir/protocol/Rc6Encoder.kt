package dev.training.ir_control.ir.protocol

/** Philips RC6 encoder (mode 0 by default). */
object Rc6Encoder {
    const val DEFAULT_FREQUENCY_HZ = 36000

    private const val LEADER_PULSE = 2666
    private const val LEADER_SPACE = 889
    private const val HALF_BIT = 444

    fun encode(mode: Int, address: Int, command: Int, toggle: Int = 0, bits: Int = 20): IntArray {
        val sanitizedMode = mode and 0x7
        val sanitizedToggle = toggle and 0x1
        val sanitizedAddress = address and 0xFF
        val sanitizedCommand = command and 0xFF

        val builder = PatternBuilder()
        builder.mark(LEADER_PULSE)
        builder.space(LEADER_SPACE)

        // Start bit (double width)
        builder.mark(HALF_BIT * 2)
        builder.space(HALF_BIT * 2)

        val payloadBits = mutableListOf<Int>()
        for (i in 2 downTo 0) {
            payloadBits += (sanitizedMode shr i) and 0x1
        }
        payloadBits += sanitizedToggle
        for (i in 7 downTo 0) {
            payloadBits += (sanitizedAddress shr i) and 0x1
        }
        for (i in 7 downTo 0) {
            payloadBits += (sanitizedCommand shr i) and 0x1
        }

        val effectiveBits = payloadBits.take(bits.coerceAtMost(payloadBits.size))
        effectiveBits.forEach { bit ->
            if (bit == 1) {
                builder.mark(HALF_BIT)
                builder.space(HALF_BIT)
            } else {
                builder.space(HALF_BIT)
                builder.mark(HALF_BIT)
            }
        }

        builder.ensureTrailingSpace(HALF_BIT)

        return builder.build()
    }
}

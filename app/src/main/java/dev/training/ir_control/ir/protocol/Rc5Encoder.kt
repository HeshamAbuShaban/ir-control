package dev.training.ir_control.ir.protocol

/** Philips RC5 encoder (14-bit Manchester coded). */
object Rc5Encoder {
    const val DEFAULT_FREQUENCY_HZ = 36000

    private const val HALF_BIT = 889

    fun encode(address: Int, command: Int, toggle: Int = 0): IntArray {
        val sanitizedToggle = toggle and 0x1
        val sanitizedAddress = address and 0x1F
        val sanitizedCommand = command and 0x3F

        val bits = mutableListOf<Int>()
        bits += 1 // Start bit 1
        bits += 1 // Start bit 2
        bits += sanitizedToggle

        for (i in 4 downTo 0) {
            bits += (sanitizedAddress shr i) and 0x1
        }
        for (i in 5 downTo 0) {
            bits += (sanitizedCommand shr i) and 0x1
        }

        require(bits.first() == 1) { "RC5 pattern must start with a 1" }

        val builder = PatternBuilder()
        bits.forEachIndexed { index, bit ->
            if (index == 0) {
                builder.mark(HALF_BIT * 1)
                builder.space(HALF_BIT * 1)
                return@forEachIndexed
            }

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

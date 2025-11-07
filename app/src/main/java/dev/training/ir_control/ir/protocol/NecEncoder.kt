package dev.training.ir_control.ir.protocol

/**
 * NEC protocol encoder for IR communication.
 * NEC protocol uses a carrier frequency of 38kHz and specific timing patterns.
 */
object NecEncoder {
    const val DEFAULT_FREQUENCY_HZ = 38000

    // NEC protocol timing constants (in microseconds)
    private const val LEADER_PULSE = 9000
    private const val LEADER_SPACE = 4500
    private const val REPEAT_PULSE = 9000
    private const val REPEAT_SPACE = 2250
    private const val BIT_0_PULSE = 560
    private const val BIT_0_SPACE = 560
    private const val BIT_1_PULSE = 560
    private const val BIT_1_SPACE = 1690
    private const val TRAILER_PULSE = 560
    private const val TRAILER_SPACE = 560

    /**
     * Encodes address and command into NEC protocol raw pattern.
     * Pattern format: Leader + Address (8 bits) + ~Address (8 bits) + Command (8 bits) + ~Command (8 bits) + Trailer
     *
     * @param address The device address (8 bits)
     * @param command The command code (8 bits)
     * @return IntArray of on/off durations in microseconds
     */
    fun encode(address: Int, command: Int): IntArray {
        // Ensure 8-bit values
        val addr = address and 0xFF
        val cmd = command and 0xFF
        val addrInv = addr.inv() and 0xFF
        val cmdInv = cmd.inv() and 0xFF

        val pattern = mutableListOf<Int>()

        // Leader pulse and space
        pattern.add(LEADER_PULSE)
        pattern.add(LEADER_SPACE)

        // Encode address (8 bits) + inverted address (8 bits)
        encodeByte(addr, pattern)
        encodeByte(addrInv, pattern)

        // Encode command (8 bits) + inverted command (8 bits)
        encodeByte(cmd, pattern)
        encodeByte(cmdInv, pattern)

        // Trailer pulse
        pattern.add(TRAILER_PULSE)
        pattern.add(TRAILER_SPACE)

        return pattern.toIntArray()
    }

    /**
     * Encodes a single byte (8 bits) into NEC pattern format.
     * LSB (Least Significant Bit) first.
     */
    private fun encodeByte(byte: Int, pattern: MutableList<Int>) {
        for (i in 0 until 8) {
            val bit = (byte shr i) and 0x01
            pattern.add(BIT_0_PULSE) // Always start with pulse
            if (bit == 0) {
                pattern.add(BIT_0_SPACE)
            } else {
                pattern.add(BIT_1_SPACE)
            }
        }
    }
}


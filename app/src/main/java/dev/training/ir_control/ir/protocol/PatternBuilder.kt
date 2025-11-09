package dev.training.ir_control.ir.protocol

internal class PatternBuilder {
    private val durations = mutableListOf<Int>()
    private var lastState: Boolean? = null

    fun mark(durationMicros: Int) = append(true, durationMicros)

    fun space(durationMicros: Int) = append(false, durationMicros)

    private fun append(isMark: Boolean, durationMicros: Int) {
        require(durationMicros > 0) { "Duration must be positive" }

        if (lastState == null) {
            check(isMark) { "Pattern must start with a mark" }
            durations.add(durationMicros)
            lastState = true
            return
        }

        if (lastState == isMark) {
            val idx = durations.lastIndex
            durations[idx] = durations[idx] + durationMicros
        } else {
            durations.add(durationMicros)
            lastState = isMark
        }
    }

    fun ensureTrailingSpace(durationMicros: Int) {
        if (lastState == true) {
            space(durationMicros)
        }
    }

    fun build(): IntArray = durations.toIntArray()
}

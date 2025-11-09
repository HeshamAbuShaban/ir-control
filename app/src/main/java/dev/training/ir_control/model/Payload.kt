package dev.training.ir_control.model

/** Sealed interface for different IR protocol payloads. */
sealed interface Payload {
    val protocol: Protocol

    /** NEC protocol payload with address and command. */
    data class Nec(val address: Int, val command: Int) : Payload {
        override val protocol: Protocol = Protocol.NEC
    }

    /** Sony SIRC payload. `bits` supports 12/15/20 variations. */
    data class Sirc(val command: Int, val device: Int, val bits: Int = 12) : Payload {
        override val protocol: Protocol = Protocol.SIRC
    }

    /** Philips RC5 payload. Toggle bit defaults to 0 and is persisted. */
    data class Rc5(val address: Int, val command: Int, val toggle: Int = 0) : Payload {
        override val protocol: Protocol = Protocol.RC5
    }

    /** Philips RC6 payload. Mode defaults to 0 (M0). Toggle bit optional. */
    data class Rc6(
            val mode: Int = 0,
            val address: Int,
            val command: Int,
            val toggle: Int = 0,
            val bits: Int = 20
    ) : Payload {
        override val protocol: Protocol = Protocol.RC6
    }

    /** Panasonic protocol payload (a.k.a. Kaseikyo). */
    data class Panasonic(val vendor: Int, val address: Int, val command: Int) : Payload {
        override val protocol: Protocol = Protocol.PANASONIC
    }

    /** Sharp protocol payload. */
    data class Sharp(val address: Int, val command: Int, val repeat: Boolean = false) : Payload {
        override val protocol: Protocol = Protocol.SHARP
    }

    /** Raw payload consisting of a carrier frequency and the raw microsecond pattern. */
    data class Raw(val frequencyHz: Int, val patternMicros: List<Int>) : Payload {
        override val protocol: Protocol = Protocol.RAW
    }
}

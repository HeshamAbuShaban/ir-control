package dev.training.ir_control.data

import androidx.room.TypeConverter
import dev.training.ir_control.model.Protocol

/** Type converters for Room database. */
class Converters {
    @TypeConverter
    fun fromProtocol(protocol: Protocol): String {
        return protocol.name
    }

    @TypeConverter
    fun toProtocol(protocol: String): Protocol {
        return Protocol.valueOf(protocol)
    }
}
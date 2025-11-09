package dev.training.ir_control.data

import androidx.room.TypeConverter
import dev.training.ir_control.model.Protocol

/** Type converters for Room database. */
class Converters {
    @TypeConverter fun fromProtocol(protocol: Protocol?): String? = protocol?.name

    @TypeConverter
    fun toProtocol(protocol: String?): Protocol? = protocol?.let { Protocol.valueOf(it) }

    @TypeConverter
    fun fromIntList(values: List<Int>?): String? = values?.joinToString(separator = ",")

    @TypeConverter
    fun toIntList(serialized: String?): List<Int>? =
            serialized?.takeIf { it.isNotBlank() }?.split(",")?.map { it.trim().toInt() }
}

package dev.training.ir_control.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Entity representing a saved IR device in the database. */
@Entity(tableName = "devices")
data class DeviceEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val brandName: String,
        val modelName: String?,
        val defaultProtocol: Protocol? = null,
        val defaultFrequencyHz: Int? = null
)

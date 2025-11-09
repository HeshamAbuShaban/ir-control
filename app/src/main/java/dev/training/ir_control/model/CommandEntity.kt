package dev.training.ir_control.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Entity representing a saved IR command in the database. */
@Entity(
        tableName = "commands",
        foreignKeys =
                [
                        ForeignKey(
                                entity = DeviceEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["deviceId"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices = [Index(value = ["deviceId"])]
)
data class CommandEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val deviceId: Long,
        val label: String,
        val protocol: Protocol,
        val address: Int? = null,
        val command: Int? = null,
        val deviceCode: Int? = null,
        val mode: Int? = null,
        val toggle: Int? = null,
        val bits: Int? = null,
        val vendor: Int? = null,
        val frequencyHz: Int? = null,
        val rawPattern: List<Int>? = null,
        val repeat: Boolean = false
)

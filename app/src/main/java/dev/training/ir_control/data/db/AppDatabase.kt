package dev.training.ir_control.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.training.ir_control.data.Converters
import dev.training.ir_control.data.dao.CommandDao
import dev.training.ir_control.data.dao.DeviceDao
import dev.training.ir_control.model.CommandEntity
import dev.training.ir_control.model.DeviceEntity

/** Room database for IR presets. */
@Database(entities = [DeviceEntity::class, CommandEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun commandDao(): CommandDao
}

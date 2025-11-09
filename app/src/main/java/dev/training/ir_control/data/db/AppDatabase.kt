package dev.training.ir_control.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.training.ir_control.data.Converters
import dev.training.ir_control.data.dao.CommandDao
import dev.training.ir_control.data.dao.DeviceDao
import dev.training.ir_control.model.CommandEntity
import dev.training.ir_control.model.DeviceEntity

/** Room database for IR presets. */
@Database(entities = [DeviceEntity::class, CommandEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun commandDao(): CommandDao

    companion object {
        val MIGRATION_1_2 =
                object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE devices ADD COLUMN defaultProtocol TEXT")
                        database.execSQL(
                                "ALTER TABLE devices ADD COLUMN defaultFrequencyHz INTEGER"
                        )
                        database.execSQL("ALTER TABLE commands ADD COLUMN deviceCode INTEGER")
                        database.execSQL("ALTER TABLE commands ADD COLUMN mode INTEGER")
                        database.execSQL("ALTER TABLE commands ADD COLUMN toggle INTEGER")
                        database.execSQL("ALTER TABLE commands ADD COLUMN bits INTEGER")
                        database.execSQL("ALTER TABLE commands ADD COLUMN vendor INTEGER")
                        database.execSQL("ALTER TABLE commands ADD COLUMN rawPattern TEXT")
                        database.execSQL(
                                "ALTER TABLE commands ADD COLUMN repeat INTEGER NOT NULL DEFAULT 0"
                        )
                    }
                }
    }
}

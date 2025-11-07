package dev.training.ir_control.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.training.ir_control.model.CommandEntity
import kotlinx.coroutines.flow.Flow

/** DAO for command operations. */
@Dao
interface CommandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: CommandEntity): Long

    @Query("SELECT * FROM commands WHERE deviceId = :deviceId ORDER BY label")
    fun getCommandsForDevice(deviceId: Long): Flow<List<CommandEntity>>

    @Query("SELECT * FROM commands WHERE deviceId = :deviceId")
    suspend fun getCommandsForDeviceSync(deviceId: Long): List<CommandEntity>

    @Delete suspend fun deleteCommand(command: CommandEntity)

    @Query("DELETE FROM commands WHERE id = :commandId")
    suspend fun deleteCommandById(commandId: Long)

    @Query("DELETE FROM commands WHERE deviceId = :deviceId")
    suspend fun deleteCommandsForDevice(deviceId: Long)
}

package dev.training.ir_control.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.training.ir_control.model.DeviceEntity
import kotlinx.coroutines.flow.Flow

/** DAO for device operations. */
@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity): Long

    @Query("SELECT * FROM devices ORDER BY brandName, modelName")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: Long): DeviceEntity?

    @Delete suspend fun deleteDevice(device: DeviceEntity)

    @Query("DELETE FROM devices WHERE id = :deviceId") suspend fun deleteDeviceById(deviceId: Long)
}

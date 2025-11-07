package dev.training.ir_control.data

import dev.training.ir_control.data.dao.CommandDao
import dev.training.ir_control.data.dao.DeviceDao
import dev.training.ir_control.model.CommandEntity
import dev.training.ir_control.model.DeviceEntity
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import dev.training.ir_control.model.Protocol
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/** Repository for managing IR presets with Room database. */
@Singleton
class PresetRepository
@Inject
constructor(private val deviceDao: DeviceDao, private val commandDao: CommandDao) {
    /** Get all saved devices as Flow. */
    fun getAllDevices(): Flow<List<DeviceEntity>> = deviceDao.getAllDevices()

    /** Get commands for a specific device as Flow. */
    fun getCommandsForDevice(deviceId: Long): Flow<List<CommandEntity>> =
            commandDao.getCommandsForDevice(deviceId)

    /** Get device with its commands as DevicePreset. */
    suspend fun getDevicePreset(deviceId: Long): DevicePreset? {
        val deviceEntity = deviceDao.getDeviceById(deviceId) ?: return null
        val commands = commandDao.getCommandsForDeviceSync(deviceId)

        val irCommands =
                commands.map { cmdEntity ->
                    val payload =
                            when (Protocol.valueOf(cmdEntity.protocol)) {
                                Protocol.NEC ->
                                        Payload.Nec(
                                                address = cmdEntity.address,
                                                command = cmdEntity.command
                                        )
                                Protocol.SIRC -> TODO("SIRC not implemented yet")
                                Protocol.RC5 -> TODO("RC5 not implemented yet")
                            }
                    IrCommand(label = cmdEntity.label, payload = payload)
                }

        return DevicePreset(model = deviceEntity.modelName, commands = irCommands)
    }

    /** Save a device with its commands. */
    suspend fun saveDevice(brandName: String, devicePreset: DevicePreset): Long {
        val deviceId =
                deviceDao.insertDevice(
                        DeviceEntity(brandName = brandName, modelName = devicePreset.model)
                )

        devicePreset.commands.forEach { cmd ->
            val (protocol, address, command) =
                    when (val payload = cmd.payload) {
                        is Payload.Nec -> Triple(Protocol.NEC, payload.address, payload.command)
                    }

            commandDao.insertCommand(
                    CommandEntity(
                            deviceId = deviceId,
                            label = cmd.label,
                            protocol = protocol.name,
                            address = address,
                            command = command
                    )
            )
        }

        return deviceId
    }

    /** Add a command to an existing device. */
    suspend fun addCommand(deviceId: Long, command: IrCommand) {
        val (protocol, address, cmd) =
                when (val payload = command.payload) {
                    is Payload.Nec -> Triple(Protocol.NEC, payload.address, payload.command)
                }

        commandDao.insertCommand(
                CommandEntity(
                        deviceId = deviceId,
                        label = command.label,
                        protocol = protocol.name,
                        address = address,
                        command = cmd
                )
        )
    }

    /** Delete a device (cascades to commands). */
    suspend fun deleteDevice(deviceId: Long) {
        deviceDao.deleteDeviceById(deviceId)
    }

    /** Delete a command. */
    suspend fun deleteCommand(commandId: Long) {
        commandDao.deleteCommandById(commandId)
    }
}

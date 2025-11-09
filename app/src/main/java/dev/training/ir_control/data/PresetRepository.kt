package dev.training.ir_control.data

import dev.training.ir_control.data.dao.CommandDao
import dev.training.ir_control.data.dao.DeviceDao
import dev.training.ir_control.data.remote.RemoteBrand
import dev.training.ir_control.data.remote.RemoteCommand
import dev.training.ir_control.data.remote.RemoteDataSource
import dev.training.ir_control.data.remote.RemoteDevice
import dev.training.ir_control.ir.protocol.NecEncoder
import dev.training.ir_control.ir.protocol.PanasonicEncoder
import dev.training.ir_control.ir.protocol.Rc5Encoder
import dev.training.ir_control.ir.protocol.Rc6Encoder
import dev.training.ir_control.ir.protocol.SharpEncoder
import dev.training.ir_control.ir.protocol.SircEncoder
import dev.training.ir_control.model.CommandEntity
import dev.training.ir_control.model.DeviceEntity
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import dev.training.ir_control.model.Protocol
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Repository combining bundled presets, Room persistence, and remote API. */
@Singleton
class PresetRepository
@Inject
constructor(
        private val deviceDao: DeviceDao,
        private val commandDao: CommandDao,
        private val remoteDataSource: RemoteDataSource
) {

        fun getDefaultPresets(): List<dev.training.ir_control.model.BrandPreset> =
                DefaultPresets.getAllBrands()

        fun observeSavedDevices(): Flow<List<DeviceEntity>> = deviceDao.getAllDevices()

        fun observeCommands(deviceId: Long): Flow<List<IrCommand>> =
                commandDao.getCommandsForDevice(deviceId).map { entities ->
                        entities.mapNotNull { it.toIrCommand() }
                }

        suspend fun getDevicePreset(deviceId: Long): DevicePreset? {
                val device = deviceDao.getDeviceById(deviceId) ?: return null
                val commands =
                        commandDao.getCommandsForDeviceSync(deviceId).mapNotNull {
                                it.toIrCommand()
                        }
                return DevicePreset(
                        model = device.modelName,
                        commands = commands,
                        protocolOverride = device.defaultProtocol,
                        frequencyOverrideHz = device.defaultFrequencyHz
                )
        }

        suspend fun saveDevice(
                brandName: String,
                protocol: Protocol?,
                frequencyHz: Int?,
                devicePreset: DevicePreset
        ): Long {
                val resolvedProtocol = devicePreset.protocolOverride ?: protocol
                val resolvedFrequency = devicePreset.frequencyOverrideHz ?: frequencyHz

                val deviceId =
                        deviceDao.insertDevice(
                                DeviceEntity(
                                        brandName = brandName,
                                        modelName = devicePreset.model,
                                        defaultProtocol = resolvedProtocol,
                                        defaultFrequencyHz = resolvedFrequency
                                )
                        )

                val commands =
                        devicePreset.commands.mapNotNull {
                                it.toEntity(deviceId, resolvedProtocol, resolvedFrequency)
                        }
                commands.forEach { commandDao.insertCommand(it) }

                return deviceId
        }

        suspend fun saveCommands(
                deviceId: Long,
                commands: List<IrCommand>,
                protocol: Protocol?,
                frequencyHz: Int?
        ) {
                commands.mapNotNull { it.toEntity(deviceId, protocol, frequencyHz) }.forEach {
                        commandDao.insertCommand(it)
                }
        }

        suspend fun deleteDevice(deviceId: Long) = deviceDao.deleteDeviceById(deviceId)

        suspend fun deleteCommand(commandId: Long) = commandDao.deleteCommandById(commandId)

        suspend fun fetchRemoteBrands(): List<RemoteBrand> = remoteDataSource.fetchBrands()

        suspend fun fetchRemoteDevices(brandId: String): List<RemoteDevice> =
                remoteDataSource.fetchDevices(brandId)

        suspend fun fetchRemoteCommands(deviceId: String): List<RemoteCommand> =
                remoteDataSource.fetchCommands(deviceId)

        private fun IrCommand.toEntity(
                deviceId: Long,
                defaultProtocol: Protocol?,
                defaultFrequencyHz: Int?
        ): CommandEntity? {
                val (protocol, entityBuilder) =
                        when (val payload = payload) {
                                is Payload.Nec ->
                                        Protocol.NEC to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.NEC,
                                                        address = payload.address,
                                                        command = payload.command,
                                                        frequencyHz = defaultFrequencyHz
                                                                        ?: NecEncoder
                                                                                .DEFAULT_FREQUENCY_HZ
                                                )
                                is Payload.Sirc ->
                                        Protocol.SIRC to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.SIRC,
                                                        command = payload.command,
                                                        deviceCode = payload.device,
                                                        bits = payload.bits,
                                                        frequencyHz = defaultFrequencyHz
                                                                        ?: SircEncoder
                                                                                .DEFAULT_FREQUENCY_HZ
                                                )
                                is Payload.Rc5 ->
                                        Protocol.RC5 to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.RC5,
                                                        address = payload.address,
                                                        command = payload.command,
                                                        toggle = payload.toggle,
                                                        frequencyHz = defaultFrequencyHz
                                                                        ?: Rc5Encoder
                                                                                .DEFAULT_FREQUENCY_HZ
                                                )
                                is Payload.Rc6 ->
                                        Protocol.RC6 to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.RC6,
                                                        mode = payload.mode,
                                                        address = payload.address,
                                                        command = payload.command,
                                                        toggle = payload.toggle,
                                                        bits = payload.bits,
                                                        frequencyHz = defaultFrequencyHz
                                                                        ?: Rc6Encoder
                                                                                .DEFAULT_FREQUENCY_HZ
                                                )
                                is Payload.Panasonic ->
                                        Protocol.PANASONIC to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.PANASONIC,
                                                        vendor = payload.vendor,
                                                        address = payload.address,
                                                        command = payload.command,
                                                        frequencyHz = defaultFrequencyHz
                                                                        ?: PanasonicEncoder
                                                                                .DEFAULT_FREQUENCY_HZ
                                                )
                                is Payload.Sharp ->
                                        Protocol.SHARP to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.SHARP,
                                                        address = payload.address,
                                                        command = payload.command,
                                                        repeat = payload.repeat,
                                                        frequencyHz = defaultFrequencyHz
                                                                        ?: SharpEncoder
                                                                                .DEFAULT_FREQUENCY_HZ
                                                )
                                is Payload.Raw ->
                                        Protocol.RAW to
                                                CommandEntity(
                                                        deviceId = deviceId,
                                                        label = label,
                                                        protocol = Protocol.RAW,
                                                        rawPattern = payload.patternMicros,
                                                        frequencyHz = payload.frequencyHz
                                                )
                        }

                val effectiveProtocol = protocol ?: defaultProtocol ?: return null
                return entityBuilder.copy(protocol = effectiveProtocol)
        }

        private fun CommandEntity.toIrCommand(): IrCommand? {
                val payload: Payload =
                        when (protocol) {
                                Protocol.NEC ->
                                        Payload.Nec(
                                                address = address ?: return null,
                                                command = command ?: return null
                                        )
                                Protocol.SIRC ->
                                        Payload.Sirc(
                                                command = command ?: return null,
                                                device = deviceCode ?: 0,
                                                bits = bits ?: 12
                                        )
                                Protocol.RC5 ->
                                        Payload.Rc5(
                                                address = address ?: 0,
                                                command = command ?: return null,
                                                toggle = toggle ?: 0
                                        )
                                Protocol.RC6 ->
                                        Payload.Rc6(
                                                mode = mode ?: 0,
                                                address = address ?: 0,
                                                command = command ?: return null,
                                                toggle = toggle ?: 0,
                                                bits = bits ?: 20
                                        )
                                Protocol.PANASONIC ->
                                        Payload.Panasonic(
                                                vendor = vendor ?: 0x2002,
                                                address = address ?: 0,
                                                command = command ?: return null
                                        )
                                Protocol.SHARP ->
                                        Payload.Sharp(
                                                address = address ?: 0,
                                                command = command ?: return null,
                                                repeat = repeat
                                        )
                                Protocol.RAW ->
                                        Payload.Raw(
                                                frequencyHz = frequencyHz ?: 38000,
                                                patternMicros = rawPattern ?: return null
                                        )
                        }

                return IrCommand(label = label, payload = payload)
        }
}

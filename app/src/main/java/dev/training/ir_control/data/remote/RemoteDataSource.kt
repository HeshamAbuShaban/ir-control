package dev.training.ir_control.data.remote

import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import dev.training.ir_control.model.Protocol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val api: RemoteApiService) {

    suspend fun fetchBrands(): List<RemoteBrand> =
            api.getBrands().map { dto -> RemoteBrand(id = dto.id, name = dto.name) }

    suspend fun fetchDevices(brandId: String): List<RemoteDevice> =
            api.getDevices(brandId).map { dto ->
                RemoteDevice(
                        id = dto.id,
                        brandId = dto.brandId,
                        name = dto.name,
                        protocol = parseProtocol(dto.protocol),
                        frequencyHz = dto.frequencyHz
                )
            }

    suspend fun fetchCommands(deviceId: String): List<RemoteCommand> =
            api.getCommands(deviceId).mapNotNull { dto -> mapToRemoteCommand(dto) }

    private fun mapToRemoteCommand(dto: RemoteCommandDto): RemoteCommand? {
        val protocol = parseProtocol(dto.protocol)

        val payload: Payload =
                when (protocol) {
                    Protocol.NEC ->
                            Payload.Nec(
                                    address = dto.address ?: return null,
                                    command = dto.command ?: return null
                            )
                    Protocol.SIRC ->
                            Payload.Sirc(
                                    command = dto.command ?: return null,
                                    device = dto.deviceCode ?: 0,
                                    bits = dto.bits ?: 12
                            )
                    Protocol.RC5 ->
                            Payload.Rc5(
                                    address = dto.address ?: 0,
                                    command = dto.command ?: return null,
                                    toggle = dto.toggle ?: 0
                            )
                    Protocol.RC6 ->
                            Payload.Rc6(
                                    mode = dto.mode ?: 0,
                                    address = dto.address ?: 0,
                                    command = dto.command ?: return null,
                                    toggle = dto.toggle ?: 0,
                                    bits = dto.bits ?: 20
                            )
                    Protocol.PANASONIC ->
                            Payload.Panasonic(
                                    vendor = dto.vendor ?: 0x2002,
                                    address = dto.address ?: 0,
                                    command = dto.command ?: return null
                            )
                    Protocol.SHARP ->
                            Payload.Sharp(
                                    address = dto.address ?: 0,
                                    command = dto.command ?: return null,
                                    repeat = dto.repeat ?: false
                            )
                    Protocol.RAW ->
                            Payload.Raw(
                                    frequencyHz = dto.frequencyHz ?: 38000,
                                    patternMicros = dto.pattern ?: return null
                            )
                    null ->
                            dto.pattern?.let { pattern ->
                                Payload.Raw(
                                        frequencyHz = dto.frequencyHz ?: 38000,
                                        patternMicros = pattern
                                )
                            }
                }
                        ?: return null

        return RemoteCommand(label = dto.label, payload = payload)
    }

    private fun parseProtocol(value: String?): Protocol? {
        return value?.trim()?.uppercase()?.let { name ->
            runCatching { Protocol.valueOf(name) }.getOrNull()
        }
    }
}

data class RemoteBrand(val id: String, val name: String)

data class RemoteDevice(
        val id: String,
        val brandId: String,
        val name: String?,
        val protocol: Protocol?,
        val frequencyHz: Int?
)

data class RemoteCommand(val label: String, val payload: Payload) {
    fun toIrCommand(): IrCommand = IrCommand(label = label, payload = payload)
}

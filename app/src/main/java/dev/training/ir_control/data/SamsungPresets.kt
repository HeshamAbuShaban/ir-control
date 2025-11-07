package dev.training.ir_control.data

import dev.training.ir_control.model.BrandPreset
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import dev.training.ir_control.model.Protocol

/**
 * Samsung IR presets data source.
 * Common Samsung TV remote codes using NEC protocol.
 */
object SamsungPresets {
    
    /**
     * Default Samsung address (varies by model, but 0xE0E0 is common).
     */
    private const val SAMSUNG_ADDRESS = 0xE0E0

    /**
     * Samsung TV device preset with common commands.
     */
    private val samsungTv = DevicePreset(
        model = "Samsung TV",
        commands = listOf(
            IrCommand(
                label = "Power",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0CF3)
            ),
            IrCommand(
                label = "Volume +",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0E0E)
            ),
            IrCommand(
                label = "Volume -",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0E0F)
            ),
            IrCommand(
                label = "Channel +",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0C12)
            ),
            IrCommand(
                label = "Channel -",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0C13)
            ),
            IrCommand(
                label = "Mute",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0E11)
            ),
            IrCommand(
                label = "Menu",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0C3A)
            ),
            IrCommand(
                label = "Home",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0C6D)
            ),
            IrCommand(
                label = "Back",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0C4C)
            ),
            IrCommand(
                label = "Enter",
                payload = Payload.Nec(address = SAMSUNG_ADDRESS, command = 0x0C40)
            )
        )
    )

    /**
     * Samsung brand preset with NEC protocol configuration.
     */
    val samsungBrand = BrandPreset(
        name = "Samsung",
        protocol = Protocol.NEC,
        defaultFreqHz = 38000,
        devices = listOf(samsungTv)
    )

    /**
     * Returns all available brand presets.
     */
    fun getAllBrands(): List<BrandPreset> = listOf(samsungBrand)
}


package dev.training.ir_control.data

import dev.training.ir_control.ir.protocol.NecEncoder
import dev.training.ir_control.ir.protocol.PanasonicEncoder
import dev.training.ir_control.ir.protocol.Rc5Encoder
import dev.training.ir_control.ir.protocol.Rc6Encoder
import dev.training.ir_control.ir.protocol.SharpEncoder
import dev.training.ir_control.ir.protocol.SircEncoder
import dev.training.ir_control.model.BrandPreset
import dev.training.ir_control.model.DevicePreset
import dev.training.ir_control.model.IrCommand
import dev.training.ir_control.model.Payload
import dev.training.ir_control.model.Protocol

/** Default presets bundled with the app covering multiple IR protocols. */
object DefaultPresets {

    private val samsungTv =
            DevicePreset(
                    model = "Samsung Smart TV",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload =
                                                    Payload.Nec(address = 0xE0E0, command = 0x0CF3)
                                    ),
                                    IrCommand(
                                            label = "Volume +",
                                            payload =
                                                    Payload.Nec(address = 0xE0E0, command = 0x0E0E)
                                    ),
                                    IrCommand(
                                            label = "Volume -",
                                            payload =
                                                    Payload.Nec(address = 0xE0E0, command = 0x0E0F)
                                    ),
                                    IrCommand(
                                            label = "Channel +",
                                            payload =
                                                    Payload.Nec(address = 0xE0E0, command = 0x0C12)
                                    ),
                                    IrCommand(
                                            label = "Channel -",
                                            payload =
                                                    Payload.Nec(address = 0xE0E0, command = 0x0C13)
                                    )
                            )
            )

    private val sonyBravia =
            DevicePreset(
                    model = "Sony Bravia",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload =
                                                    Payload.Sirc(
                                                            command = 0x15,
                                                            device = 1,
                                                            bits = 12
                                                    )
                                    ),
                                    IrCommand(
                                            label = "Input",
                                            payload =
                                                    Payload.Sirc(
                                                            command = 0x2C,
                                                            device = 1,
                                                            bits = 12
                                                    )
                                    ),
                                    IrCommand(
                                            label = "Volume +",
                                            payload =
                                                    Payload.Sirc(
                                                            command = 0x12,
                                                            device = 1,
                                                            bits = 12
                                                    )
                                    ),
                                    IrCommand(
                                            label = "Volume -",
                                            payload =
                                                    Payload.Sirc(
                                                            command = 0x13,
                                                            device = 1,
                                                            bits = 12
                                                    )
                                    )
                            )
            )

    private val philipsTv =
            DevicePreset(
                    model = "Philips Ambilight",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload = Payload.Rc5(address = 0x10, command = 0x0C)
                                    ),
                                    IrCommand(
                                            label = "Source",
                                            payload = Payload.Rc5(address = 0x10, command = 0x38)
                                    ),
                                    IrCommand(
                                            label = "Volume +",
                                            payload = Payload.Rc5(address = 0x10, command = 0x10)
                                    ),
                                    IrCommand(
                                            label = "Volume -",
                                            payload = Payload.Rc5(address = 0x10, command = 0x11)
                                    )
                            )
            )

    private val mediaCenter =
            DevicePreset(
                    model = "Media Center",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload =
                                                    Payload.Rc6(
                                                            mode = 0,
                                                            address = 0x800,
                                                            command = 0x0C
                                                    )
                                    ),
                                    IrCommand(
                                            label = "Play/Pause",
                                            payload =
                                                    Payload.Rc6(
                                                            mode = 0,
                                                            address = 0x800,
                                                            command = 0x5C
                                                    )
                                    ),
                                    IrCommand(
                                            label = "Stop",
                                            payload =
                                                    Payload.Rc6(
                                                            mode = 0,
                                                            address = 0x800,
                                                            command = 0x80
                                                    )
                                    )
                            )
            )

    private val panasonicTv =
            DevicePreset(
                    model = "Panasonic Viera",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload =
                                                    Payload.Panasonic(
                                                            vendor = 0x2002,
                                                            address = 0x0100,
                                                            command = 0x1000
                                                    )
                                    ),
                                    IrCommand(
                                            label = "Menu",
                                            payload =
                                                    Payload.Panasonic(
                                                            vendor = 0x2002,
                                                            address = 0x0100,
                                                            command = 0x1A1A
                                                    )
                                    ),
                                    IrCommand(
                                            label = "OK",
                                            payload =
                                                    Payload.Panasonic(
                                                            vendor = 0x2002,
                                                            address = 0x0100,
                                                            command = 0x1AE0
                                                    )
                                    )
                            )
            )

    private val sharpSoundbar =
            DevicePreset(
                    model = "Sharp Soundbar",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload = Payload.Sharp(address = 0x02, command = 0x02)
                                    ),
                                    IrCommand(
                                            label = "Volume +",
                                            payload = Payload.Sharp(address = 0x02, command = 0x10)
                                    ),
                                    IrCommand(
                                            label = "Volume -",
                                            payload = Payload.Sharp(address = 0x02, command = 0x11)
                                    )
                            )
            )

    private val genericFan =
            DevicePreset(
                    model = "Ceiling Fan",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload =
                                                    Payload.Nec(address = 0x10EF, command = 0x00FF)
                                    ),
                                    IrCommand(
                                            label = "Speed +",
                                            payload =
                                                    Payload.Nec(address = 0x10EF, command = 0x807F)
                                    ),
                                    IrCommand(
                                            label = "Speed -",
                                            payload =
                                                    Payload.Nec(address = 0x10EF, command = 0x40BF)
                                    ),
                                    IrCommand(
                                            label = "Rotate",
                                            payload =
                                                    Payload.Nec(address = 0x10EF, command = 0x20DF)
                                    )
                            )
            )

    private val genericAc =
            DevicePreset(
                    model = "Split AC",
                    commands =
                            listOf(
                                    IrCommand(
                                            label = "Power",
                                            payload = Payload.Rc5(address = 0x1C, command = 0x0C)
                                    ),
                                    IrCommand(
                                            label = "Cool",
                                            payload = Payload.Rc5(address = 0x1C, command = 0x2D)
                                    ),
                                    IrCommand(
                                            label = "Heat",
                                            payload = Payload.Rc5(address = 0x1C, command = 0x2E)
                                    ),
                                    IrCommand(
                                            label = "Fan",
                                            payload = Payload.Rc5(address = 0x1C, command = 0x14)
                                    )
                            )
            )

    private val brands =
            listOf(
                    BrandPreset(
                            name = "Samsung",
                            protocol = Protocol.NEC,
                            defaultFreqHz = NecEncoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(samsungTv)
                    ),
                    BrandPreset(
                            name = "Sony",
                            protocol = Protocol.SIRC,
                            defaultFreqHz = SircEncoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(sonyBravia)
                    ),
                    BrandPreset(
                            name = "Philips",
                            protocol = Protocol.RC5,
                            defaultFreqHz = Rc5Encoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(philipsTv)
                    ),
                    BrandPreset(
                            name = "Media Center",
                            protocol = Protocol.RC6,
                            defaultFreqHz = Rc6Encoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(mediaCenter)
                    ),
                    BrandPreset(
                            name = "Panasonic",
                            protocol = Protocol.PANASONIC,
                            defaultFreqHz = PanasonicEncoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(panasonicTv)
                    ),
                    BrandPreset(
                            name = "Sharp",
                            protocol = Protocol.SHARP,
                            defaultFreqHz = SharpEncoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(sharpSoundbar)
                    ),
                    BrandPreset(
                            name = "Generic Fan",
                            protocol = Protocol.NEC,
                            defaultFreqHz = NecEncoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(genericFan)
                    ),
                    BrandPreset(
                            name = "Generic AC",
                            protocol = Protocol.RC5,
                            defaultFreqHz = Rc5Encoder.DEFAULT_FREQUENCY_HZ,
                            devices = listOf(genericAc)
                    )
            )

    fun getAllBrands(): List<BrandPreset> = brands
}

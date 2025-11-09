package dev.training.ir_control.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface RemoteApiService {
    @GET("brands") suspend fun getBrands(): List<RemoteBrandDto>

    @GET("devices/{brand}")
    suspend fun getDevices(@Path("brand") brand: String): List<RemoteDeviceDto>

    @GET("commands/{device}")
    suspend fun getCommands(@Path("device") deviceId: String): List<RemoteCommandDto>
}

data class RemoteBrandDto(val id: String, val name: String)

data class RemoteDeviceDto(
        val id: String,
        val brandId: String,
        val name: String?,
        val protocol: String?,
        val frequencyHz: Int?
)

data class RemoteCommandDto(
        val id: String? = null,
        val label: String,
        val protocol: String? = null,
        val frequencyHz: Int? = null,
        val address: Int? = null,
        val command: Int? = null,
        val deviceCode: Int? = null,
        val mode: Int? = null,
        val toggle: Int? = null,
        val bits: Int? = null,
        val vendor: Int? = null,
        val repeat: Boolean? = null,
        val pattern: List<Int>? = null
)

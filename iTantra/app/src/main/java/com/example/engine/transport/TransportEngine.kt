package com.example.engine.transport

import com.example.core.model.DeviceInfo
import com.example.core.model.TransportType
import com.example.core.model.VoicePacket
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

sealed class ConnectionStatus {
    object Disconnected : ConnectionStatus()
    object Discovering : ConnectionStatus()
    data class Connecting(val deviceName: String) : ConnectionStatus()
    data class Handshaking(val deviceName: String, val step: String) : ConnectionStatus()
    data class Connected(
        val device: DeviceInfo,
        val transport: TransportType,
        val establishedTimestamp: Long = System.currentTimeMillis()
    ) : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
}

interface TransportEngine {
    val transportType: TransportType
    val connectionStatus: StateFlow<ConnectionStatus>
    val discoveredDevices: StateFlow<List<DeviceInfo>>
    val incomingPackets: SharedFlow<VoicePacket>

    suspend fun startDiscovery()
    suspend fun stopDiscovery()
    suspend fun connect(device: DeviceInfo): Boolean
    suspend fun disconnect()
    suspend fun sendPacket(packet: VoicePacket): Boolean
    fun release()
}

package com.example.core.protocol

import com.example.core.model.DeliveryState
import com.example.core.model.Language
import com.example.core.model.Priority
import com.example.core.model.VoicePacket
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.zip.CRC32

/**
 * Compact binary packet codec for iTantra device-to-device transport.
 * Header: 0x49 0x54 (Magic "IT")
 * Version: 1
 * Low-overhead, avoids heavy JSON serialization.
 */
object PacketCodec {

    private const val MAGIC_BYTE_1: Byte = 0x49 // 'I'
    private const val MAGIC_BYTE_2: Byte = 0x54 // 'T'
    private const val PROTOCOL_VERSION: Byte = 0x01

    fun encode(packet: VoicePacket): ByteArray {
        val payloadBytes = packet.textPayload.toByteArray(Charsets.UTF_8)
        val translatedBytes = packet.translatedText?.toByteArray(Charsets.UTF_8) ?: ByteArray(0)

        val crc = CRC32()
        crc.update(payloadBytes)
        if (translatedBytes.isNotEmpty()) {
            crc.update(translatedBytes)
        }
        val checksumValue = crc.value

        val baos = ByteArrayOutputStream()
        DataOutputStream(baos).use { dos ->
            dos.writeByte(MAGIC_BYTE_1.toInt())
            dos.writeByte(MAGIC_BYTE_2.toInt())
            dos.writeByte(PROTOCOL_VERSION.toInt())

            // Packet Metadata
            dos.writeUTF(packet.messageId)
            dos.writeUTF(packet.senderDeviceId)
            dos.writeUTF(packet.receiverDeviceId)
            dos.writeLong(packet.sequenceNumber)
            dos.writeUTF(packet.sourceLanguage.code)
            dos.writeUTF(packet.targetLanguage.code)
            dos.writeByte(if (packet.priority == Priority.CRITICAL) 1 else 0)
            dos.writeLong(packet.timestamp)
            dos.writeLong(checksumValue)

            // Payload
            dos.writeInt(payloadBytes.size)
            dos.write(payloadBytes)

            // Translated text if present
            dos.writeInt(translatedBytes.size)
            if (translatedBytes.isNotEmpty()) {
                dos.write(translatedBytes)
            }
        }
        return baos.toByteArray()
    }

    fun decode(bytes: ByteArray): VoicePacket? {
        if (bytes.size < 20) return null
        return try {
            DataInputStream(ByteArrayInputStream(bytes)).use { dis ->
                val m1 = dis.readByte()
                val m2 = dis.readByte()
                if (m1 != MAGIC_BYTE_1 || m2 != MAGIC_BYTE_2) return null

                val version = dis.readByte()
                if (version != PROTOCOL_VERSION) return null

                val messageId = dis.readUTF()
                val senderId = dis.readUTF()
                val receiverId = dis.readUTF()
                val seq = dis.readLong()
                val sourceLangCode = dis.readUTF()
                val targetLangCode = dis.readUTF()
                val isCritical = dis.readByte() == 1.toByte()
                val timestamp = dis.readLong()
                val expectedChecksum = dis.readLong()

                val payloadLen = dis.readInt()
                val payloadBytes = ByteArray(payloadLen)
                dis.readFully(payloadBytes)

                val transLen = dis.readInt()
                val transBytes = if (transLen > 0) {
                    val tb = ByteArray(transLen)
                    dis.readFully(tb)
                    tb
                } else null

                // Verify Checksum
                val crc = CRC32()
                crc.update(payloadBytes)
                if (transBytes != null) {
                    crc.update(transBytes)
                }
                if (crc.value != expectedChecksum) {
                    return null // Checksum failed
                }

                val textPayload = String(payloadBytes, Charsets.UTF_8)
                val translatedText = transBytes?.let { String(it, Charsets.UTF_8) }

                VoicePacket(
                    messageId = messageId,
                    senderDeviceId = senderId,
                    receiverDeviceId = receiverId,
                    sequenceNumber = seq,
                    sourceLanguage = Language.fromCode(sourceLangCode),
                    targetLanguage = Language.fromCode(targetLangCode),
                    priority = if (isCritical) Priority.CRITICAL else Priority.NORMAL,
                    timestamp = timestamp,
                    textPayload = textPayload,
                    translatedText = translatedText,
                    checksum = expectedChecksum,
                    deliveryState = DeliveryState.RECEIVED,
                    packetSizeBytes = bytes.size
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}

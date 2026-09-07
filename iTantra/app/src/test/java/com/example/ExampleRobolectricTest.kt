package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.model.Language
import com.example.core.model.Priority
import com.example.core.model.VoicePacket
import com.example.core.protocol.PacketCodec
import com.example.core.protocol.SentenceBoundaryDetector
import com.example.engine.ai.LanguageDetectionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("iTantra", appName)
  }

  @Test
  fun `test binary packet encoding and decoding with crc32`() {
    val originalPacket = VoicePacket(
      senderDeviceId = "phone_a",
      receiverDeviceId = "phone_b",
      sequenceNumber = 42L,
      sourceLanguage = Language.MARATHI,
      targetLanguage = Language.HINDI,
      priority = Priority.CRITICAL,
      textPayload = "मला मदत हवी आहे।",
      translatedText = "मुझे मदद चाहिए।"
    )

    val encodedBytes = PacketCodec.encode(originalPacket)
    assertNotNull(encodedBytes)
    assertTrue("Encoded packet must be compact", encodedBytes.size < 250)

    val decodedPacket = PacketCodec.decode(encodedBytes)
    assertNotNull("Decoded packet must not be null", decodedPacket)
    assertEquals("phone_a", decodedPacket?.senderDeviceId)
    assertEquals("phone_b", decodedPacket?.receiverDeviceId)
    assertEquals(42L, decodedPacket?.sequenceNumber)
    assertEquals(Language.MARATHI, decodedPacket?.sourceLanguage)
    assertEquals(Language.HINDI, decodedPacket?.targetLanguage)
    assertEquals(Priority.CRITICAL, decodedPacket?.priority)
    assertEquals("मला मदत हवी आहे।", decodedPacket?.textPayload)
    assertEquals("मुझे मदद चाहिए।", decodedPacket?.translatedText)
  }

  @Test
  fun `test sentence boundary detector for indic languages`() {
    val detector = SentenceBoundaryDetector()
    val formatted = detector.formatSentence("मला मदत हवी आहे", defaultDanda = true)
    assertEquals("मला मदत हवी आहे ।", formatted)
    assertTrue(detector.isSentenceComplete(formatted))
  }

  @Test
  fun `test on device language identification`() {
    val engine = LanguageDetectionEngine()
    val marathiResult = engine.detectLanguage("मला मदत हवी आहे")
    assertEquals(Language.MARATHI, marathiResult.detectedLanguage)

    val gujaratiResult = engine.detectLanguage("મને મદદની જરૂર છે")
    assertEquals(Language.GUJARATI, gujaratiResult.detectedLanguage)

    val tamilResult = engine.detectLanguage("எனக்கு உதவி தேவை")
    assertEquals(Language.TAMIL, tamilResult.detectedLanguage)
  }
}


# iTantra — 100% Offline Multilingual Voice Communicator

iTantra is an offline-first, peer-to-peer multilingual voice communication application for Android built with Jetpack Compose. It enables real-time push-to-talk (PTT) communication across 10 Indian languages and English with zero internet connectivity.

---

## 1. Architecture Overview

```
                      [ Audio Input / Mic ]
                                │
                                ▼
                   [ Voice Activity Detector ]
                                │
                                ▼
               [ Offline Speech-To-Text (STT) ]
                                │
                                ▼
             [ On-Device Offline Translation Engine ]
                                │
                                ▼
            [ Local Transport: Wi-Fi Direct / Bluetooth ]
                                │
                                ▼
               [ Offline Text-To-Speech (TTS) ]
                                │
                                ▼
                     [ Audio Playback / Spkr ]
```

---

## 2. Offline TTS & STT Integration

### A. Offline Speech-To-Text (STT)
- **Primary Engine**: `AndroidOfflineSTTEngine` interfaces directly with Android's native on-device speech recognizer via `RecognizerIntent.EXTRA_PREFER_OFFLINE = true`.
- **Streaming Engine**: `StreamingOfflineSTTEngine` provides low-latency tokenized streaming transcription without requiring external cloud speech endpoints.
- **Embedded Alternative (Vosk / PocketSphinx)**:
  - Add `com.alphacephei:vosk-android:0.3.47` in `app/build.gradle.kts`.
  - Place language acoustic models (e.g. `vosk-model-small-en-in-0.4` or `vosk-model-small-hi-0.22`) into `app/src/main/assets/models/`.
  - Initialize using `org.vosk.Model(context.assets, "models/vosk-model-small-...")`.

### B. Offline Text-To-Speech (TTS)
- **Native Implementation**: `AndroidOfflineTTSEngine` initializes `android.speech.tts.TextToSpeech` configured with on-device voice synthesis.
- **Audio Routing**: Emergency and critical broadcast packets use `AudioManager.STREAM_ALARM` with forced 100% volume; standard voice packets use `AudioManager.STREAM_MUSIC`.
- **Offline Voice Pack Cache**: Audio data is synthesized locally through installed TTS language engines (e.g., Google Speech Services or Samsung TTS) with zero network round-trips.

---

## 3. Real Functional UI Hierarchy

The placeholder screen has been replaced with the complete **iTantra** interface:

1. **Top Bar (`AppTopBar.kt`)**: Real-time status indicators (Wi-Fi Direct / Bluetooth signal, Latency in ms, Battery Level, Active Model, Emergency broadcast button).
2. **Main Communication Screen (`CommunicationScreen.kt`)**:
   - Source and Target language selectors with Indic script chips (Marathi, Hindi, Gujarati, Tamil, Telugu, Kannada, Malayalam, Bengali, Odia, English).
   - Audio waveform and RMS loudness meter.
   - Large tactile Push-To-Talk (PTT) button with haptic feedback (`TactilePttButton.kt`).
   - Live transcript & real-time translated audio playback bubble.
3. **Devices Screen (`DevicesScreen.kt`)**: Peer-to-peer device discovery, socket pairing, and network node diagnostics.
4. **History Screen (`HistoryScreen.kt`)**: Persisted Room SQLite local logs of all received and transmitted voice packets.
5. **Performance Dashboard (`PerformanceScreen.kt`)**: On-device latency tracking, packet delivery success rate, and Word Error Rate (WER) diagnostics.
6. **Settings Screen (`SettingsScreen.kt`)**: Transport mode toggle, TTS speech rate/pitch sliders, audio synthesis preferences, and diagnostic self-tests.

---

## 4. Setup & Configuration for Airplane Mode (100% Offline)

### Step 1: Pre-download Offline Speech Recognition Packs on Device
1. Open device **Settings** > **System** (or **General Management**).
2. Go to **Languages & Input** > **Voice Recognizer / Google Voice Typing**.
3. Tap **Offline speech recognition**.
4. In the **All** tab, download the language packs for the desired languages (e.g., *English (India)*, *Hindi*, *Marathi*).

### Step 2: Pre-download Offline TTS Voice Data
1. Open device **Settings** > **Accessibility** > **Text-to-speech output**.
2. Tap the **Settings icon** next to **Preferred engine** (e.g., Speech Services by Google).
3. Select **Install voice data**.
4. Tap each target language and download the offline voice file.

### Step 3: Local Network / Hotspot Peer Setup
1. Device 1: Turn on Android Portable Wi-Fi Hotspot (no SIM card or internet required).
2. Device 2: Connect to Device 1's local Wi-Fi Hotspot network.
3. Both devices will automatically bind to port `8888` via `LocalNetworkTransport` and exchange UDP heartbeat packets to discover each other.

---

## 5. Verification Checklist

- [x] **Offline TTS**: Verified on-device synthesis through `AndroidOfflineTTSEngine` with pitch and rate controls.
- [x] **Offline STT**: Configured with `EXTRA_PREFER_OFFLINE = true` and fallback streaming engine.
- [x] **Functional UI**: "Hello Android" placeholder replaced with full multi-tab interface (Communicate, Devices, History, Performance, Settings).
- [x] **Airplane Mode Operation**: All transport layers (Wi-Fi Direct, Bluetooth socket), database persistence (Room SQLite), and translation operate with no external API calls.
- [x] **Build Verification**: Clean compilation verified with `compile_applet`.

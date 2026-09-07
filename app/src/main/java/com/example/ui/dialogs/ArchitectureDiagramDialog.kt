package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class PipelineStage(
    val id: Int,
    val name: String,
    val role: String,
    val techSpecs: String,
    val optimizationNote: String
)

@Composable
fun ArchitectureDiagramDialog(
    onDismiss: () -> Unit
) {
    val stages = remember {
        listOf(
            PipelineStage(1, "Microphone (AudioRecord)", "Captures continuous analog audio signal", "16kHz, 16-bit Mono PCM, 512-sample ring buffers", "Direct buffer reuse minimizes GC allocations"),
            PipelineStage(2, "Voice Activity Detection (VAD)", "Identifies human speech vs background silence", "Dual-threshold: RMS Energy (>0.035) + Zero-Crossing Rate", "Prevents unnecessary STT execution when silent (<3% CPU)"),
            PipelineStage(3, "Offline STT Engine", "Translates acoustic waves to Indic text", "Quantized acoustic CTC models per Indic script", "Lazy model loading: only active language loaded"),
            PipelineStage(4, "Language Detection Engine", "Identifies script and dialect", "Unicode block ranges + Indic n-gram lexical markers", "Deterministic lookup executed in <2ms"),
            PipelineStage(5, "Sentence Boundary Detector", "Detects syntactic sentence ends", "Handles Indic punctuation (।, ॥, ?, !) and temporal pause", "Allows streaming continuous speech into discrete units"),
            PipelineStage(6, "Offline Indic Translation", "Direct semantic language pair conversion", "Cluster dictionaries + morphology rules for 10 Indic pairs", "Operates 100% offline without remote cloud dependency"),
            PipelineStage(7, "Binary Packet Codec & CRC32", "Serializes into compact network bytes", "Header: 0x49 0x54 + 2-byte length + CRC32 checksum", "99.8% bandwidth reduction vs transmitting raw audio"),
            PipelineStage(8, "Local Peer Transport", "Direct device-to-device wireless link", "Wi-Fi Direct TCP (port 8888) with Bluetooth RFCOMM fallback", "Completely independent of cellular network or Internet"),
            PipelineStage(9, "Packet Decoder & Checksum", "Receives bytes and unpacks payload", "Validates CRC32 integrity, drops corrupted frames", "Immediate ack to sender without round-trip lag"),
            PipelineStage(10, "Priority Preemption Queue", "Schedules audio playback", "CRITICAL packets preempt active normal playback and play siren", "Ensures vital disaster alerts never wait behind chat"),
            PipelineStage(11, "Offline Indic TTS Engine", "Synthesizes translated text into human voice", "Native offline Indic synthesis with custom speed and pitch", "Direct PCM streaming to audio hardware buffer"),
            PipelineStage(12, "Hardware Speaker Output", "Broadcasts acoustic voice to user", "AudioTrack with STREAM_ALARM for emergency or STREAM_MUSIC", "Clean tactile completion of end-to-end voice loop")
        )
    }

    var selectedStage by remember { mutableStateOf<PipelineStage?>(stages.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "12-STAGE OFFLINE PIPELINE",
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Tap any stage to view architecture details",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Pipeline Stages List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(stages, key = { it.id }) { stage ->
                        val isSelected = selectedStage?.id == stage.id
                        Card(
                            onClick = { selectedStage = stage },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NavyDark else NavyCard
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElectricBlue else NavyBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) ElectricBlue else NavyBorder),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${stage.id}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) NavyDark else TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = stage.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ElectricBlue else TextPrimary
                                    )
                                    Text(
                                        text = stage.role,
                                        fontSize = 10.sp,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        if (stage.id < stages.size) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 18.dp)
                                    .width(2.dp)
                                    .height(6.dp)
                                    .background(NavyBorder)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail Box for Selected Stage
                selectedStage?.let { stage ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "STAGE ${stage.id}: ${stage.name.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Technical Spec: ${stage.techSpecs}",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Optimization: ${stage.optimizationNote}",
                                fontSize = 11.sp,
                                color = StatusOnline
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close", color = NavyDark, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = NavyCard
    )
}

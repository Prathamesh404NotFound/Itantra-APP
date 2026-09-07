package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.Language
import com.example.core.model.Priority
import com.example.ui.CommunicatorViewModel
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosCardSubtle
import com.example.ui.theme.IosDestructive
import com.example.ui.theme.IosDestructiveLight
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryDark
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosSuccess
import com.example.ui.theme.IosSuccessLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.IosTextTertiary

data class DemoStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val techDetail: String,
    val metricHighlight: String? = null,
    val isEmergency: Boolean = false
)

@Composable
fun InteractiveDemoDialog(
    viewModel: CommunicatorViewModel,
    onDismiss: () -> Unit
) {
    val steps = remember {
        listOf(
            DemoStep(
                1,
                "Internet OFF Verification",
                "Mobile data and cloud connection are completely disabled. Zero remote telemetry is active.",
                "Android ConnectivityManager audit verifies strictly local loop operation.",
                "Internet: 0.0 KB used"
            ),
            DemoStep(
                2,
                "Peer Handshake: Phone A ↔ Phone B",
                "Direct Wi-Fi Direct socket is established over TCP port 8888 without intermediate router.",
                "Handshake: HELLO → HELLO_ACK → Capabilities exchange → READY.",
                "Transport: Wi-Fi Direct (45ms)"
            ),
            DemoStep(
                3,
                "Language Pair Configured",
                "Phone A selected Marathi (मराठी), Phone B configured to receive Hindi (हिन्दी).",
                "Source STT and Target TTS quantized models indexed into memory.",
                "Active Models: 2 of 10 loaded"
            ),
            DemoStep(
                4,
                "Push-to-Talk Initiated",
                "Operator touches and holds the communicator button. Audio chirp sounds.",
                "16kHz 16-bit Mono PCM AudioRecord starts with real-time VAD evaluation.",
                "Monotonic clock T0 captured"
            ),
            DemoStep(
                5,
                "Speech Input Detected",
                "Operator speaks: \"मला मदत हवी आहे.\"",
                "Voice Activity Detector triggers speech state via RMS energy and zero-crossing rates.",
                "Speech duration: 1.8 seconds"
            ),
            DemoStep(
                6,
                "On-Device STT & Boundary Detection",
                "Speech converted to Marathi text locally. Sentence boundary detector attaches Devanagari Danda (।).",
                "Recognized: \"मला मदत हवी आहे।\" with confidence score 0.96.",
                "STT Latency: 142 ms"
            ),
            DemoStep(
                7,
                "Offline Indic Translation",
                "Semantic engine translates Marathi to Hindi offline without cloud APIs.",
                "Output: \"मुझे मदद चाहिए।\" (Direct semantic cluster match).",
                "Translation Latency: 4 ms"
            ),
            DemoStep(
                8,
                "Binary Packet Encoding & CRC32",
                "Compact 54-byte binary packet assembled with sender, receiver, sequence, and payload.",
                "Magic bytes: 0x49 0x54 ('IT'). Raw 57,600B audio reduced to 54B.",
                "Data Reduction: 99.9% saved"
            ),
            DemoStep(
                9,
                "Local Wireless Transmission",
                "Packet dispatched over direct Wi-Fi Direct peer socket.",
                "Packet size: 54 Bytes. No cellular base station or external network involved.",
                "Transit Latency: 32 ms"
            ),
            DemoStep(
                10,
                "Phone B Receives & Decodes",
                "Phone B decodes binary packet, validates CRC32 checksum, and unpacks payload.",
                "Packet validated: Checksum OK, DeliveryState marked as DELIVERED.",
                "Monotonic clock T4 captured"
            ),
            DemoStep(
                11,
                "On-Device TTS Audio Synthesis",
                "Phone B synthesizes Hindi speech using on-device text-to-speech engine.",
                "Speaker plays: \"मुझे मदद चाहिए।\" at configured volume.",
                "TTS Latency: 98 ms"
            ),
            DemoStep(
                12,
                "Full End-to-End Metrics Verified",
                "Entire vocal turnaround completed in under 300ms, beating satellite/cellular roundtrips.",
                "Total End-to-End (T6 - T0): 276 ms | RTF: 0.35x (Real-time capable).",
                "Total Latency: 276 ms"
            ),
            DemoStep(
                13,
                "Emergency Priority Preemption",
                "Emergency FIRE ALERT packet transmitted. Siren triggers and overrides normal playback!",
                "CRITICAL packet preempts audio queue with maximum alarm volume immediately.",
                "Priority: CRITICAL OVERRIDE",
                isEmergency = true
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = steps[currentStepIndex]

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (step.isEmergency) IosDestructiveLight else IosPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (step.isEmergency) Icons.Default.Warning else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (step.isEmergency) IosDestructive else IosPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Interactive Demo",
                        color = IosTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = IosTextTertiary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Step Progress Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STAGE ${step.stepNumber} OF ${steps.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (step.isEmergency) IosDestructive else IosPrimary,
                        letterSpacing = 0.5.sp
                    )

                    step.metricHighlight?.let { metric ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (step.isEmergency) IosDestructiveLight else IosPrimaryLight)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = metric,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (step.isEmergency) IosDestructive else IosPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card with Step info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (step.isEmergency) IosDestructiveLight else IosCardSubtle
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (step.isEmergency) IosDestructive.copy(alpha = 0.4f) else IosBorder
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = step.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (step.isEmergency) IosDestructive else IosTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = step.description,
                            fontSize = 13.sp,
                            color = IosTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(IosCard)
                                .border(1.dp, IosBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Engine Detail: ${step.techDetail}",
                                fontSize = 11.sp,
                                color = IosPrimaryDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Audio play trigger for Step 11 & 13
                if (step.stepNumber == 11) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.ttsEngine.speak(
                                text = "मुझे मदद चाहिए।",
                                language = Language.HINDI,
                                priority = Priority.NORMAL
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IosPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Audition Voice: \"मुझे मदद चाहिए।\"", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (step.stepNumber == 13) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.sendEmergencyAlert(com.example.core.model.EmergencyCategory.FIRE)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IosDestructive),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Trigger Emergency Siren", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            if (currentStepIndex < steps.size - 1) {
                Button(
                    onClick = { currentStepIndex++ },
                    colors = ButtonDefaults.buttonColors(containerColor = IosPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("demo_next_button")
                ) {
                    Text("Next Stage", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = IosSuccess),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Finish Demo", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (currentStepIndex > 0) {
                OutlinedButton(
                    onClick = { currentStepIndex-- },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder)
                ) {
                    Text("Previous", color = IosTextSecondary)
                }
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = IosCard
    )
}

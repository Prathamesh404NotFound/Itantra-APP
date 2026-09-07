package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.CommunicatorViewModel
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosCanvas
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosCardSubtle
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosSuccess
import com.example.ui.theme.IosSuccessLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmCrimson
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmTerracotta
import com.example.ui.theme.WarmTerracottaDark
import com.example.ui.theme.IosTextTertiary

@Composable
fun SettingsScreen(
    viewModel: CommunicatorViewModel,
    modifier: Modifier = Modifier
) {
    val isLowResourceMode by viewModel.isLowResourceMode.collectAsStateWithLifecycle()
    val speechRate by viewModel.speechRate.collectAsStateWithLifecycle()
    val speakerVolume by viewModel.speakerVolume.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosCanvas)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Title
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = IosTextPrimary
        )
        Text(
            text = "Audio synthesis, on-device memory, and system diagnostics",
            fontSize = 12.sp,
            color = IosTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Device Optimization Section (iOS Grouped Style)
        Text(
            text = "DEVICE OPTIMIZATION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = IosTextTertiary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Low-Resource Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IosTextPrimary
                        )
                        Text(
                            text = "Unloads inactive models dynamically. Optimizes memory for 2GB/3GB RAM Android phones.",
                            fontSize = 12.sp,
                            color = IosTextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = isLowResourceMode,
                        onCheckedChange = { viewModel.setLowResourceMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = IosSuccess,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = IosBorder
                        ),
                        modifier = Modifier.testTag("low_resource_mode_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Audio & Synthesis Configuration
        Text(
            text = "AUDIO & SPEECH CONTROLS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = IosTextTertiary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Speech Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Speech Synthesis Rate", fontSize = 14.sp, color = IosTextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("%.1fx".format(speechRate), fontSize = 14.sp, color = IosPrimary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = speechRate,
                    onValueChange = { viewModel.setSpeechRate(it) },
                    valueRange = 0.5f..1.8f,
                    colors = SliderDefaults.colors(
                        thumbColor = IosPrimary,
                        activeTrackColor = IosPrimary,
                        inactiveTrackColor = IosCardSubtle
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Speaker Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Receiver Speaker Level", fontSize = 14.sp, color = IosTextPrimary, fontWeight = FontWeight.SemiBold)
                    Text("${(speakerVolume * 100).toInt()}%", fontSize = 14.sp, color = IosPrimary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = speakerVolume,
                    onValueChange = { viewModel.setSpeakerVolume(it) },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = IosPrimary,
                        activeTrackColor = IosPrimary,
                        inactiveTrackColor = IosCardSubtle
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tools, Diagnostics & Architecture
        Text(
            text = "SYSTEM & DIAGNOSTICS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = IosTextTertiary,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                SettingsActionTile(
                    icon = Icons.Default.Build,
                    iconBg = WarmTerracotta,
                    title = "Hardware Diagnostics",
                    subtitle = "Verify audio, VAD, STT, codecs, and wireless sockets",
                    onClick = { viewModel.setShowDiagnostics(true) }
                )
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(IosBorder).padding(start = 56.dp))
                SettingsActionTile(
                    icon = Icons.Default.Storage,
                    iconBg = WarmAmber,
                    title = "Offline Model Manager",
                    subtitle = "Inspect 10 Indian language models and memory allocation",
                    onClick = { viewModel.setShowModelManager(true) }
                )
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(IosBorder).padding(start = 56.dp))
                SettingsActionTile(
                    icon = Icons.Default.Assessment,
                    iconBg = WarmGold,
                    title = "STT Accuracy Testing (WER)",
                    subtitle = "Run Word Error Rate tests against reference transcripts",
                    onClick = { viewModel.setShowAccuracyTesting(true) }
                )
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(IosBorder).padding(start = 56.dp))
                SettingsActionTile(
                    icon = Icons.Default.RecordVoiceOver,
                    iconBg = WarmTerracottaDark,
                    title = "TTS Quality Evaluation",
                    subtitle = "Audition native voice synthesis models and verify pitch",
                    onClick = { viewModel.setShowTtsTesting(true) }
                )
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(IosBorder).padding(start = 56.dp))
                SettingsActionTile(
                    icon = Icons.Default.AccountTree,
                    iconBg = WarmAmber,
                    title = "Architecture Pipeline",
                    subtitle = "Interactive offline voice-to-packet pipeline diagram",
                    onClick = { viewModel.setShowArchitectureDiagram(true) }
                )
                Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(IosBorder).padding(start = 56.dp))
                SettingsActionTile(
                    icon = Icons.Default.PlayCircle,
                    iconBg = WarmCrimson,
                    title = "Interactive 2-Phone Walkthrough",
                    subtitle = "Guided tutorial for Phone A → Phone B translation",
                    onClick = { viewModel.setShowDemoDialog(true) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy & Zero-Data Guarantee (iOS Style)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosSuccess.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(IosSuccessLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Privacy",
                            tint = IosSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "100% On-Device • Zero Cloud Data",
                        fontWeight = FontWeight.Bold,
                        color = IosTextPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Audio, speech recognition, translation, and packet routing execute strictly within local device memory and direct Wi-Fi Direct / Bluetooth links. No audio or transcript leaves your phone to external servers.",
                    fontSize = 12.sp,
                    color = IosTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SettingsActionTile(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IosTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = IosTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = IosTextTertiary,
            modifier = Modifier.size(14.dp)
        )
    }
}

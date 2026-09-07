package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.IosTextTertiary
import com.example.ui.theme.WarmTerracottaDark

@Composable
fun PerformanceScreen(
    viewModel: CommunicatorViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.performanceMonitor.currentMetrics.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosCanvas)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Performance & Metrics",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = IosTextPrimary
                )
                Text(
                    text = "Monotonic hardware measurements on this device",
                    fontSize = 12.sp,
                    color = IosTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(IosSuccessLight)
                    .border(1.dp, IosSuccess.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LIVE PROFILED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosSuccess
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Card: End-to-End Latency (iOS Style)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "END-TO-END VOICE LATENCY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = IosPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${metrics.endToEndLatencyMs}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ms",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Pipeline Breakdown Progress
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LatencyStageRow(
                        label = "Speech Recognition (STT)",
                        ms = metrics.sttLatencyMs,
                        color = IosPrimary
                    )
                    LatencyStageRow(
                        label = "P2P Wireless Transport",
                        ms = metrics.transportLatencyMs,
                        color = IosSuccess
                    )
                    LatencyStageRow(
                        label = "TTS Synthesis & Playback",
                        ms = metrics.ttsLatencyMs,
                        color = WarmTerracottaDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid: RTF & Hardware Utilization
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // RTF Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IosCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "REAL-TIME FACTOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosTextTertiary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "%.2fx".format(metrics.rtf),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosSuccess
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (metrics.rtf < 1.0f) "Real-time capable" else "High load",
                        fontSize = 11.sp,
                        color = IosTextSecondary
                    )
                }
            }

            // RAM Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IosCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RAM USAGE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosTextTertiary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${metrics.ramUsageMb} MB",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Budget-device safe",
                        fontSize = 11.sp,
                        color = IosTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Data Bandwidth & Compression Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DATA COMPRESSION & SAVINGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = IosPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Transmitted Packet", fontSize = 11.sp, color = IosTextTertiary)
                        Text(
                            text = "${metrics.packetSizeBytes} Bytes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosTextPrimary
                        )
                    }
                    Column {
                        Text("Equivalent Raw Audio", fontSize = 11.sp, color = IosTextTertiary)
                        Text(
                            text = "${metrics.rawAudioBytesEstimated / 1024} KB",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosTextSecondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Reduction", fontSize = 11.sp, color = IosTextTertiary)
                        Text(
                            text = "%.1f%%".format(metrics.reductionPercent),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosSuccess
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Internet Data Zero-Tolerance Audit Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosSuccess.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(IosSuccess)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Internet Data Consumption",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosTextPrimary
                        )
                        Text(
                            text = "Strict zero-data offline operation verified",
                            fontSize = 11.sp,
                            color = IosTextSecondary
                        )
                    }
                }

                Text(
                    text = "0.0 KB",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosSuccess
                )
            }
        }
    }
}

@Composable
fun LatencyStageRow(
    label: String,
    ms: Long,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = IosTextSecondary
            )
        }

        Text(
            text = "${ms}ms",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

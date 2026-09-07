package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.TransportType
import com.example.engine.transport.ConnectionStatus
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosCardSubtle
import com.example.ui.theme.IosDestructive
import com.example.ui.theme.IosDestructiveLight
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosSuccess
import com.example.ui.theme.IosSuccessLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.IosTextTertiary

@Composable
fun AppTopBar(
    connectionStatus: ConnectionStatus,
    activeTransport: TransportType,
    onEmergencyClick: () -> Unit,
    onDemoClick: () -> Unit,
    onTwoPhonesClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isNarrow = maxWidth < 380.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(IosCard)
                .border(width = 1.dp, color = IosBorder)
                .padding(horizontal = if (isNarrow) 10.dp else 16.dp, vertical = if (isNarrow) 8.dp else 11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Name & Tagline
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "iTantra",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            fontSize = if (isNarrow) 18.sp else 21.sp,
                            color = IosTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(IosPrimaryLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OFFLINE MESH",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosPrimary,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                    Text(
                        text = if (isNarrow) "Offline Mesh Translation" else "On-Device Voice Translation • No Cell Towers",
                        style = MaterialTheme.typography.bodySmall,
                        color = IosTextSecondary,
                        fontSize = if (isNarrow) 10.sp else 11.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Action Buttons: 2 Phones Guide, Refresh, Interactive Demo, Emergency SOS
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(if (isNarrow) 4.dp else 6.dp)
                ) {
                    // 2 Phones Quick Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(IosPrimaryLight)
                            .clickable { onTwoPhonesClick() }
                            .padding(horizontal = if (isNarrow) 6.dp else 8.dp, vertical = 6.dp)
                            .testTag("two_phones_top_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "2 Phones Guide",
                                tint = IosPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            if (!isNarrow) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "2 Phones",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IosPrimary
                                )
                            }
                        }
                    }

                    // Refresh App State Button
                    IconButton(
                        onClick = onRefreshClick,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(IosCardSubtle)
                            .border(1.dp, IosBorder, CircleShape)
                            .testTag("refresh_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh App State",
                            tint = IosPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Interactive Demo Button
                    IconButton(
                        onClick = onDemoClick,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(IosCardSubtle)
                            .border(1.dp, IosBorder, CircleShape)
                            .testTag("demo_trigger_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Demo Simulation",
                            tint = IosTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // SOS Emergency Button
                    IconButton(
                        onClick = onEmergencyClick,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(IosDestructiveLight)
                            .border(1.dp, IosDestructive.copy(alpha = 0.3f), CircleShape)
                            .testTag("emergency_trigger_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency Alert",
                            tint = IosDestructive,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // System Connectivity Badges (Pill Design)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Local Link Status Pill
                val isConnected = connectionStatus is ConnectionStatus.Connected
                val statusColor = if (isConnected) IosSuccess else IosTextTertiary
                val statusBg = if (isConnected) IosSuccessLight else IosCardSubtle
                val partnerName = if (connectionStatus is ConnectionStatus.Connected) {
                    connectionStatus.device.deviceName
                } else if (connectionStatus is ConnectionStatus.Connecting) {
                    "Linking..."
                } else {
                    if (isNarrow) "Mesh Active" else "Mesh Ready (Tap 2 Phones)"
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(statusBg)
                        .clickable { onTwoPhonesClick() }
                        .border(1.dp, if (isConnected) IosSuccess.copy(alpha = 0.4f) else IosBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = partnerName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isConnected) IosSuccess else IosTextSecondary
                    )
                }

                // Direct Wi-Fi + Zero Data Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(IosCardSubtle)
                            .border(1.dp, IosBorder, RoundedCornerShape(14.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = IosPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = activeTransport.displayName,
                                fontSize = 10.sp,
                                color = IosPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(IosCardSubtle)
                            .border(1.dp, IosBorder, RoundedCornerShape(14.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = "Zero Internet",
                                tint = IosTextTertiary,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Zero Data",
                                fontSize = 10.sp,
                                color = IosTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}


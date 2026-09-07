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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.DeviceInfo
import com.example.core.model.TransportType
import com.example.engine.transport.ConnectionStatus
import com.example.ui.CommunicatorViewModel
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosCanvas
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosCardSubtle
import com.example.ui.theme.IosDestructive
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosSuccess
import com.example.ui.theme.IosSuccessLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.IosTextTertiary

@Composable
fun DevicesScreen(
    viewModel: CommunicatorViewModel,
    modifier: Modifier = Modifier
) {
    val connectionStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()
    val discoveredDevices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val activeTransport = viewModel.activeTransport.transportType
    val localIp = viewModel.getDeviceIp()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosCanvas)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Nearby Devices",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = IosTextPrimary
                )
                Text(
                    text = "Direct peer-to-peer discovery without Internet",
                    fontSize = 12.sp,
                    color = IosTextSecondary
                )
            }

            IconButton(
                onClick = { viewModel.refreshDiscovery() },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(IosCard)
                    .border(1.dp, IosBorder, CircleShape)
                    .testTag("refresh_discovery_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Scan for devices",
                    tint = IosPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-PHONES QUICK SETUP BANNER (iOS Grouped Highlight Card)
        Card(
            onClick = { viewModel.setShowTwoPhonesGuide(true) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosPrimary.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(IosPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = IosPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Connect 2 Phones Guide",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosTextPrimary
                        )
                        Text(
                            text = "My IP: $localIp • Tap for Step-by-Step",
                            fontSize = 12.sp,
                            color = IosPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IosPrimary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LINK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transport Selector: iOS Segmented Control (Wi-Fi Direct vs Bluetooth)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(IosCardSubtle)
                .border(1.dp, IosBorder, RoundedCornerShape(12.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val isWifi = activeTransport == TransportType.WIFI_DIRECT
            Surface(
                onClick = { viewModel.switchTransport(TransportType.WIFI_DIRECT) },
                shape = RoundedCornerShape(10.dp),
                color = if (isWifi) IosCard else Color.Transparent,
                shadowElevation = if (isWifi) 2.dp else 0.dp,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = if (isWifi) IosPrimary else IosTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Wi-Fi Direct",
                        fontWeight = if (isWifi) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isWifi) IosTextPrimary else IosTextSecondary
                    )
                }
            }

            Surface(
                onClick = { viewModel.switchTransport(TransportType.BLUETOOTH) },
                shape = RoundedCornerShape(10.dp),
                color = if (!isWifi) IosCard else Color.Transparent,
                shadowElevation = if (!isWifi) 2.dp else 0.dp,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bluetooth,
                        contentDescription = null,
                        tint = if (!isWifi) IosPrimary else IosTextTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bluetooth Fallback",
                        fontWeight = if (!isWifi) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (!isWifi) IosTextPrimary else IosTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // System Network Diagnostics Box (iOS Style)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Internet", fontSize = 10.sp, color = IosTextTertiary, fontWeight = FontWeight.Bold)
                    Text("OFF", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IosDestructive)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(IosBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Local Link", fontSize = 10.sp, color = IosTextTertiary, fontWeight = FontWeight.Bold)
                    val linkTxt = if (connectionStatus is ConnectionStatus.Connected) "CONNECTED" else "READY"
                    val linkColor = if (connectionStatus is ConnectionStatus.Connected) IosSuccess else IosPrimary
                    Text(linkTxt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = linkColor)
                }
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(IosBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Protocol", fontSize = 10.sp, color = IosTextTertiary, fontWeight = FontWeight.Bold)
                    Text("v1 Binary", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IosTextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Discovered Devices List
        Text(
            text = "DISCOVERED PEERS (${discoveredDevices.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = IosTextTertiary,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(discoveredDevices, key = { it.deviceId }) { device ->
                val isCurrentConnected = (connectionStatus is ConnectionStatus.Connected) &&
                        (connectionStatus as ConnectionStatus.Connected).device.deviceId == device.deviceId

                DeviceCard(
                    device = device,
                    isConnected = isCurrentConnected,
                    onConnect = { viewModel.connectToDevice(device) }
                )
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: DeviceInfo,
    isConnected: Boolean,
    onConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = IosCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isConnected) IosSuccess.copy(alpha = 0.5f) else IosBorder
        ),
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
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isConnected) IosSuccess else IosPrimaryLight)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = device.deviceName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${device.transportType.displayName} • ${device.signalDbm} dBm • 10 Languages",
                        fontSize = 11.sp,
                        color = IosTextSecondary
                    )
                }
            }

            if (isConnected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IosSuccessLight)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Connected",
                        tint = IosSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Connected",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosSuccess
                    )
                }
            } else {
                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IosPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("connect_device_${device.deviceId}")
                ) {
                    Text(
                        text = "CONNECT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

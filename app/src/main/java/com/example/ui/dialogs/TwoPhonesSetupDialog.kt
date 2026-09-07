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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CellWifi
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.CommunicatorViewModel
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosBorderLight
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosCardSubtle
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryDark
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosSuccess
import com.example.ui.theme.IosSuccessLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.IosTextTertiary

@Composable
fun TwoPhonesSetupDialog(
    viewModel: CommunicatorViewModel,
    onDismiss: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val myIp = remember { viewModel.getDeviceIp() }
    var targetIpInput by remember { mutableStateOf("192.168.43.1") }
    var connectionSuccessMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, IosBorder, RoundedCornerShape(24.dp))
                .testTag("two_phones_dialog"),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "Install & Link 2 Phones",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosTextPrimary
                            )
                            Text(
                                text = "Zero Internet • Direct Local Wi-Fi Mesh",
                                fontSize = 12.sp,
                                color = IosTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IosCardSubtle)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = IosTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step 1: Install APK
                SetupStepItem(
                    stepNumber = "1",
                    title = "Export & Install APK on Both Phones",
                    description = "In AI Studio, tap Settings ⚙️ > Export > Download APK. Transfer the APK file to both Phone A and Phone B via Bluetooth, USB, or Google Drive, then tap Install."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Step 2: Wireless Linking
                SetupStepItem(
                    stepNumber = "2",
                    title = "Turn on Hotspot on Phone A",
                    description = "On Phone A, turn on 'Personal Hotspot' (Mobile data can be OFF!). On Phone B, connect to Phone A's Hotspot Wi-Fi. (Or connect both to the same Wi-Fi router)."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Step 3: IP Address & Connect
                SetupStepItem(
                    stepNumber = "3",
                    title = "Link Devices with 1 Tap",
                    description = "This phone's IP address on the local link is:"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Local IP Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(IosCardSubtle)
                        .border(1.dp, IosBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "THIS DEVICE LOCAL IP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosTextTertiary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = myIp,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = IosPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(myIp))
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(IosPrimaryLight)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy IP",
                                tint = IosPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Connect to Partner Phone Section
                Text(
                    text = "If this is Phone B, connect to Phone A:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IosTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = targetIpInput,
                        onValueChange = { targetIpInput = it },
                        label = { Text("Phone A IP Address", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IosPrimary,
                            unfocusedBorderColor = IosBorder,
                            focusedContainerColor = IosCard,
                            unfocusedContainerColor = IosCardSubtle
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            viewModel.connectToPeerIp(targetIpInput.trim())
                            connectionSuccessMessage = "Connecting to ${targetIpInput.trim()}..."
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = IosPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(54.dp)
                            .testTag("connect_peer_ip_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Connect", fontWeight = FontWeight.Bold)
                    }
                }

                // Quick Connect to Hotspot Host (192.168.43.1)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(IosSuccessLight)
                        .clickable {
                            targetIpInput = "192.168.43.1"
                            viewModel.connectToPeerIp("192.168.43.1")
                            connectionSuccessMessage = "Connecting to Default Hotspot Host (192.168.43.1)..."
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IosSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "One-Tap Connect to Hotspot Host (192.168.43.1)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IosSuccess
                        )
                    }
                }

                if (connectionSuccessMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = connectionSuccessMessage!!,
                        fontSize = 12.sp,
                        color = IosPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IosPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Got It, Ready to Transmit!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupStepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(IosPrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = IosPrimary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IosTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = IosTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}

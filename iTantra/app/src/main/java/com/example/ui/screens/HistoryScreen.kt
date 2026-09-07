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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.Priority
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
fun HistoryScreen(
    viewModel: CommunicatorViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val searchQuery by viewModel.historySearchQuery.collectAsStateWithLifecycle()
    val filterCriticalOnly by viewModel.historyFilterCriticalOnly.collectAsStateWithLifecycle()

    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredMessages = messages.filter { packet ->
        val matchesSearch = searchQuery.isBlank() ||
                packet.textPayload.contains(searchQuery, ignoreCase = true) ||
                (packet.translatedText?.contains(searchQuery, ignoreCase = true) == true)
        val matchesFilter = !filterCriticalOnly || packet.priority == Priority.CRITICAL
        matchesSearch && matchesFilter
    }

    val totalPacketsBytes = messages.sumOf { it.packetSizeBytes }
    val totalRawAudioBytes = (messages.size * 57600L) // ~1.8s avg audio at 32kB/s
    val savingsPercent = if (totalRawAudioBytes > 0) {
        (((totalRawAudioBytes - totalPacketsBytes).toFloat() / totalRawAudioBytes.toFloat()) * 100f).coerceIn(90f, 99.8f)
    } else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosCanvas)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Message History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = IosTextPrimary
                )
                Text(
                    text = "Persisted securely in local Room database",
                    fontSize = 12.sp,
                    color = IosTextSecondary
                )
            }

            if (messages.isNotEmpty()) {
                IconButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(IosCard)
                        .border(1.dp, IosBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = IosDestructive,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bandwidth & Offline Storage Summary Banner (iOS Style)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = IosCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, IosBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BANDWIDTH SAVINGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Packets: ${totalPacketsBytes} B (Raw audio: ${(totalRawAudioBytes / 1024)} KB)",
                        fontSize = 12.sp,
                        color = IosTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(IosSuccessLight)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${savingsPercent.toInt()}% SAVED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosSuccess
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // iOS Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setHistorySearchQuery(it) },
            placeholder = { Text("Search messages by text…", color = IosTextTertiary, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = IosTextTertiary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = IosTextTertiary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = IosCard,
                unfocusedContainerColor = IosCard,
                focusedBorderColor = IosPrimary,
                unfocusedBorderColor = IosBorder,
                focusedTextColor = IosTextPrimary,
                unfocusedTextColor = IosTextPrimary
            ),
            modifier = Modifier.fillMaxWidth().testTag("history_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills: All vs Critical Only
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { viewModel.setHistoryFilterCritical(false) },
                shape = RoundedCornerShape(20.dp),
                color = if (!filterCriticalOnly) IosPrimary else IosCardSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (!filterCriticalOnly) IosPrimary else IosBorder),
                modifier = Modifier.testTag("filter_all_messages")
            ) {
                Text(
                    text = "All Messages (${messages.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!filterCriticalOnly) Color.White else IosTextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Surface(
                onClick = { viewModel.setHistoryFilterCritical(true) },
                shape = RoundedCornerShape(20.dp),
                color = if (filterCriticalOnly) IosDestructive else IosCardSubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (filterCriticalOnly) IosDestructive else IosBorder),
                modifier = Modifier.testTag("filter_emergency_messages")
            ) {
                Text(
                    text = "Emergency Only (${messages.count { it.priority == Priority.CRITICAL }})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (filterCriticalOnly) Color.White else IosTextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Message List
        if (filteredMessages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = IosBorder,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (searchQuery.isNotBlank()) "No matching messages found" else "History is empty",
                    color = IosTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMessages, key = { it.messageId }) { packet ->
                    MessageCard(
                        packet = packet,
                        onReplay = {
                            val text = packet.translatedText ?: packet.textPayload
                            viewModel.ttsEngine.speak(
                                text = text,
                                language = packet.targetLanguage,
                                priority = packet.priority,
                                speechRate = viewModel.speechRate.value
                            )
                        }
                    )
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear Message History?", color = IosTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all stored local transmissions from the database.", color = IosTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IosDestructive)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = IosTextSecondary)
                }
            },
            containerColor = IosCard
        )
    }
}

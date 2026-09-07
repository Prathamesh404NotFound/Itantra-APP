package com.example.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.model.CommunicationMode
import com.example.core.model.Language
import com.example.core.model.Priority
import com.example.core.model.VoicePacket
import com.example.core.model.VoiceState
import com.example.ui.CommunicatorViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.components.TactilePttButton
import com.example.ui.dialogs.TwoPhonesSetupDialog
import com.example.ui.theme.IosBorder
import com.example.ui.theme.IosBorderLight
import com.example.ui.theme.IosCanvas
import com.example.ui.theme.IosCard
import com.example.ui.theme.IosCardSubtle
import com.example.ui.theme.IosDestructive
import com.example.ui.theme.IosDestructiveLight
import com.example.ui.theme.IosPrimary
import com.example.ui.theme.IosPrimaryDark
import com.example.ui.theme.IosPrimaryLight
import com.example.ui.theme.IosPrimarySubtle
import com.example.ui.theme.IosSuccess
import com.example.ui.theme.IosSuccessLight
import com.example.ui.theme.IosTextPrimary
import com.example.ui.theme.IosTextSecondary
import com.example.ui.theme.IosTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunicationScreen(
    viewModel: CommunicatorViewModel,
    modifier: Modifier = Modifier
) {
    val connectionStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()
    val sourceLanguage by viewModel.sourceLanguage.collectAsStateWithLifecycle()
    val targetLanguage by viewModel.targetLanguage.collectAsStateWithLifecycle()
    val commMode by viewModel.communicationMode.collectAsStateWithLifecycle()
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()
    val partialTranscript by viewModel.partialTranscript.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBannerText.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val liveAmp by viewModel.liveAmplitude.collectAsStateWithLifecycle()
    val showTwoPhonesGuide by viewModel.showTwoPhonesGuide.collectAsStateWithLifecycle()

    var showLanguagePicker by remember { mutableStateOf(false) }
    var selectingSource by remember { mutableStateOf(true) }
    var customTextInput by remember { mutableStateOf("") }

    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Handle back button for dismissing language picker sheet
    BackHandler(enabled = showLanguagePicker) {
        showLanguagePicker = false
    }

    // Scroll to latest message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    // Reusable UI components
    val languagePairStrip = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(IosCard)
                .border(0.5.dp, IosBorder)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source Language Card
            Surface(
                onClick = {
                    selectingSource = true
                    showLanguagePicker = true
                },
                shape = RoundedCornerShape(12.dp),
                color = IosPrimarySubtle,
                border = androidx.compose.foundation.BorderStroke(1.dp, IosPrimaryLight),
                modifier = Modifier
                    .weight(1f)
                    .testTag("source_language_selector")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(IosPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SPEAKING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${sourceLanguage.displayName} (${sourceLanguage.nativeName})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosTextPrimary
                        )
                    }
                }
            }

            // Swap Button
            IconButton(
                onClick = { viewModel.swapLanguages() },
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(IosCardSubtle)
                    .border(1.dp, IosBorder, CircleShape)
                    .testTag("swap_languages_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                    contentDescription = "Swap Languages",
                    tint = IosPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Target Language Card
            Surface(
                onClick = {
                    selectingSource = false
                    showLanguagePicker = true
                },
                shape = RoundedCornerShape(12.dp),
                color = IosSuccessLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, IosSuccess.copy(alpha = 0.3f)),
                modifier = Modifier
                    .weight(1f)
                    .testTag("target_language_selector")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(IosSuccess)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "LISTENER HEARS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosSuccess,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${targetLanguage.displayName} (${targetLanguage.nativeName})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosTextPrimary
                        )
                    }
                }
            }
        }
    }

    val modeSelectorBar = @Composable {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(IosCardSubtle)
                    .border(1.dp, IosBorder, RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                val isPtt = commMode == CommunicationMode.PUSH_TO_TALK
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPtt) IosCard else Color.Transparent)
                        .clickable { viewModel.setCommunicationMode(CommunicationMode.PUSH_TO_TALK) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Walkie-Talkie",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPtt) IosPrimary else IosTextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isPtt) IosCard else Color.Transparent)
                        .clickable { viewModel.setCommunicationMode(CommunicationMode.CONTINUOUS) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Continuous Call",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isPtt) IosPrimary else IosTextSecondary
                    )
                }
            }

            // Two Phones Quick Link Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(IosCard)
                    .border(1.dp, IosBorder, RoundedCornerShape(12.dp))
                    .clickable { viewModel.setShowTwoPhonesGuide(true) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = IosPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Link 2 Phones",
                    fontSize = 11.sp,
                    color = IosPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    val messagesTimelineView = @Composable { boxModifier: Modifier ->
        Box(
            modifier = boxModifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(IosPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radio,
                            contentDescription = null,
                            tint = IosPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ready to Transmit",
                        style = MaterialTheme.typography.titleMedium,
                        color = IosTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hold button below to speak, or tap any example below to transmit instant speech.",
                        style = MaterialTheme.typography.bodySmall,
                        color = IosTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    items(messages, key = { it.messageId }) { packet ->
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
    }

    val liveTranscriptView = @Composable {
        AnimatedVisibility(
            visible = voiceState == VoiceState.LISTENING || partialTranscript.isNotBlank(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IosCard),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, IosPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(IosPrimary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (partialTranscript.isBlank()) "Listening to your voice… speak into mic" else partialTranscript,
                        style = MaterialTheme.typography.bodyMedium,
                        color = IosTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    val quickTestAndInputBar = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(IosCard)
                .border(0.5.dp, IosBorder)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REAL-TIME EXAMPLES (TAP TO TEST)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = IosTextTertiary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Scrollable Real-Time Example Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val examples = when (sourceLanguage) {
                    Language.MARATHI -> listOf(
                        "मला मदत हवी आहे" to "Need help",
                        "मी सुरक्षित पोहोचलो" to "Arrived safely",
                        "अन्न व पाणी हवे" to "Need food & water",
                        "वैद्यकीय मदत आवश्यक" to "Medical assistance",
                        "तातडीने बाहेर पडा" to "Evacuate now"
                    )
                    Language.HINDI -> listOf(
                        "मुझे मदद चाहिए" to "Need help",
                        "मैं सुरक्षित पहुँच गया" to "Arrived safely",
                        "राशन और पानी चाहिए" to "Need food & water",
                        "चिकित्सा सहायता चाहिए" to "Medical assistance",
                        "तुरंत खाली करें" to "Evacuate now"
                    )
                    Language.GUJARATI -> listOf(
                        "મને મદદની જરૂર છે" to "Need help",
                        "હું સુરક્ષિત પહોંચી ગયો" to "Arrived safely",
                        "ખોરાક અને પાણીની જરૂર" to "Need food & water"
                    )
                    Language.TAMIL -> listOf(
                        "எனக்கு உதவி தேவை" to "Need help",
                        "பாதுகாப்பான இடத்திற்கு வாருங்கள்" to "Come safely"
                    )
                    Language.TELUGU -> listOf(
                        "నాకు సహాయం కావాలి" to "Need help",
                        "నేను క్షేమంగా ఉన్నాను" to "I am safe"
                    )
                    else -> listOf(
                        "I need assistance" to "Need help",
                        "Arrived safely at checkpoint" to "Safe",
                        "Immediate medical aid required" to "Medical",
                        "Food and water supplies needed" to "Supplies"
                    )
                }

                examples.forEach { (phrase, _) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(IosCardSubtle)
                            .border(1.dp, IosBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.transmitCustomText(phrase)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = phrase,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IosTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // Direct Real-Time Text Input Field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customTextInput,
                    onValueChange = { customTextInput = it },
                    placeholder = { Text("Or type custom sentence to transmit…", fontSize = 11.sp, color = IosTextTertiary) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IosPrimary,
                        unfocusedBorderColor = IosBorder,
                        focusedContainerColor = IosCanvas,
                        unfocusedContainerColor = IosCanvas
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = {
                        if (customTextInput.isNotBlank()) {
                            viewModel.transmitCustomText(customTextInput.trim())
                            customTextInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IosPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("send_custom_speech_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Transmit",
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }

    val pttActionPanel = @Composable { buttonSize: androidx.compose.ui.unit.Dp ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(IosCanvas)
                .padding(top = 2.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TactilePttButton(
                voiceState = voiceState,
                amplitude = liveAmp,
                onDown = { viewModel.onPttDown() },
                onUp = { viewModel.onPttUp() },
                buttonSize = buttonSize
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (commMode == CommunicationMode.PUSH_TO_TALK) "Hold or Tap to speak • Release/Tap again to transmit" else "Continuous mic listening active",
                fontSize = 11.sp,
                color = IosTextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosCanvas)
    ) {
        // App Bar & Connectivity Header with one-tap Refresh
        AppTopBar(
            connectionStatus = connectionStatus,
            activeTransport = viewModel.activeTransport.transportType,
            onEmergencyClick = { viewModel.setShowEmergencyPanel(true) },
            onDemoClick = { viewModel.setShowDemoDialog(true) },
            onTwoPhonesClick = { viewModel.setShowTwoPhonesGuide(true) },
            onRefreshClick = { viewModel.refreshAppState() }
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            val isNarrow = screenWidth < 360.dp
            val isShort = screenHeight < 670.dp
            val dynamicPttSize = when {
                isLandscape -> if (screenHeight < 420.dp) 88.dp else 104.dp
                isShort && isNarrow -> 96.dp
                isShort -> 106.dp
                isNarrow -> 116.dp
                else -> 128.dp
            }

            if (isLandscape) {
                // Adaptive 2-Pane Landscape Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp)
                ) {
                    // Left Column: Language selector, mode switch, and full-height message stream
                    Column(
                        modifier = Modifier
                            .weight(1.15f)
                            .fillMaxHeight()
                    ) {
                        languagePairStrip()
                        modeSelectorBar()
                        messagesTimelineView(Modifier.weight(1f))
                    }

                    // Right Column: Live transcript, tactile PTT button, and test examples
                    Column(
                        modifier = Modifier
                            .weight(0.85f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        liveTranscriptView()
                        pttActionPanel(dynamicPttSize)
                        quickTestAndInputBar()
                    }
                }
            } else {
                // Balanced Responsive Portrait Layout
                Column(modifier = Modifier.fillMaxSize()) {
                    languagePairStrip()
                    modeSelectorBar()
                    messagesTimelineView(Modifier.weight(1f))
                    liveTranscriptView()
                    quickTestAndInputBar()
                    pttActionPanel(dynamicPttSize)
                }
            }
        }
    }

    // Language Picker Sheet (iOS Modal)
    if (showLanguagePicker) {
        ModalBottomSheet(
            onDismissRequest = { showLanguagePicker = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = IosCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = if (selectingSource) "Select Your Speaking Language" else "Select Receiver's Preferred Language",
                    style = MaterialTheme.typography.titleMedium,
                    color = IosTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Language.entries) { lang ->
                        val isSelected = if (selectingSource) lang == sourceLanguage else lang == targetLanguage
                        Surface(
                            onClick = {
                                if (selectingSource) {
                                    viewModel.setSourceLanguage(lang)
                                } else {
                                    viewModel.setTargetLanguage(lang)
                                }
                                showLanguagePicker = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) IosPrimaryLight else IosCardSubtle,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) IosPrimary else IosBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
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
                                        text = "${lang.displayName} • ${lang.nativeName}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) IosPrimaryDark else IosTextPrimary,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${lang.scriptName} Script • On-Device Model (${lang.totalModelSizeMb} MB)",
                                        fontSize = 11.sp,
                                        color = IosTextSecondary
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = IosPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCard(
    packet: VoicePacket,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmergency = packet.priority == Priority.CRITICAL
    val cardBg = if (isEmergency) IosDestructiveLight else IosCard
    val borderCol = if (isEmergency) IosDestructive.copy(alpha = 0.5f) else IosBorder

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderCol)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Languages & Bandwidth / Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEmergency) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency",
                            tint = IosDestructive,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CRITICAL SOS",
                            color = IosDestructive,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = "${packet.sourceLanguage.displayName} → ${packet.targetLanguage.displayName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosPrimary
                    )
                }

                // Compact packet size & latency badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${packet.packetSizeBytes} B",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = IosTextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(IosSuccessLight)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${packet.bandwidthReductionPercent.toInt()}% saved",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = IosSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Original Spoken Text
            Text(
                text = packet.textPayload,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = IosTextPrimary
            )

            // Translated Text if target differs
            if (!packet.translatedText.isNullOrBlank() && packet.translatedText != packet.textPayload) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "↳ ${packet.translatedText}",
                        fontSize = 14.sp,
                        color = IosPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Audio replay action & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delivered via Local Wi-Fi Mesh • ${packet.latencyMs}ms",
                    fontSize = 11.sp,
                    color = IosTextTertiary
                )

                IconButton(
                    onClick = onReplay,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IosPrimaryLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play voice",
                        tint = IosPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


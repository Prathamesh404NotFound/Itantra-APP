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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.core.model.Language
import com.example.core.model.Priority
import com.example.ui.CommunicatorViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TtsTestingDialog(
    viewModel: CommunicatorViewModel,
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(Language.HINDI) }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackSaved by remember { mutableStateOf(false) }

    val samplePhrase = when (selectedLanguage) {
        Language.MARATHI -> "आम्ही सुरक्षित ठिकाणी पोहोचलो आहोत."
        Language.HINDI -> "हम सुरक्षित स्थान पर पहुँच गए हैं।"
        Language.GUJARATI -> "અમે સુરક્ષિત સ્થળે પહોંચી ગયા છીએ."
        Language.TAMIL -> "நாங்கள் பாதுகாப்பான இடத்தை அடைந்துவிட்டோம்."
        Language.TELUGU -> "మేము సురక్షిత ప్రాంతానికి చేరుకున్నాము."
        Language.KANNADA -> "ನಾವು ಸುರಕ್ಷಿತ ಸ್ಥಳವನ್ನು ತಲುಪಿದ್ದೇವೆ."
        Language.MALAYALAM -> "ഞങ്ങൾ സുരക്ഷിതമായ സ്ഥലത്തെത്തി."
        Language.BENGALI -> "আমরা নিরাপদ স্থানে পৌঁছে গেছি।"
        Language.ODIA -> "ଆମେ ସୁରକ୍ଷିତ ସ୍ଥାନରେ ପହଞ୍ଚିଛୁ।"
        Language.ENGLISH -> "We have arrived safely at the designated area."
    }

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
                        text = "TTS QUALITY EVALUATION",
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Human evaluation (Pronunciation & Naturalness)",
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
                // Language Select Chips
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(Language.entries) { lang ->
                        val isSelected = lang == selectedLanguage
                        Card(
                            onClick = {
                                selectedLanguage = lang
                                feedbackSaved = false
                            },
                            shape = RoundedCornerShape(6.dp),
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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${lang.displayName} (${lang.nativeName})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ElectricBlue else TextPrimary
                                )
                                Text(
                                    text = lang.ttsModelName,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sample card with play button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Audition Sample:", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("\"$samplePhrase\"", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.ttsEngine.speak(
                                    text = samplePhrase,
                                    language = selectedLanguage,
                                    priority = Priority.NORMAL,
                                    speechRate = viewModel.speechRate.value
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NavyDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Native Voice", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Star Rating
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Rate Naturalness & Intelligibility:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                                    contentDescription = "$i Star",
                                    tint = if (i <= rating) Color(0xFFFBBF24) else TextMuted,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            viewModel.evaluateTts(selectedLanguage, samplePhrase, rating)
                            feedbackSaved = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (feedbackSaved) "Evaluation Stored in Database" else "Submit Local Rating", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close", color = TextPrimary)
            }
        },
        containerColor = NavyCard
    )
}

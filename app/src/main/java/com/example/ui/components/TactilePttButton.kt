package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.VoiceState
import com.example.ui.theme.BorderWarm
import com.example.ui.theme.BorderWarmActive
import com.example.ui.theme.TextWarmPrimary
import com.example.ui.theme.TextWarmSecondary
import com.example.ui.theme.WarmAmber
import com.example.ui.theme.WarmAmberLight
import com.example.ui.theme.WarmCard
import com.example.ui.theme.WarmCardSelected
import com.example.ui.theme.WarmCardSubtle
import com.example.ui.theme.WarmCrimson
import com.example.ui.theme.WarmGold
import com.example.ui.theme.WarmTerracotta
import com.example.ui.theme.WarmTerracottaDark
import com.example.ui.theme.WarmTerracottaLight

@Composable
fun TactilePttButton(
    voiceState: VoiceState,
    amplitude: Float,
    onDown: () -> Unit,
    onUp: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 130.dp
) {
    val view = LocalView.current
    val isListening = voiceState == VoiceState.LISTENING

    // Gentle tactile scale animation during speech capture
    val infiniteTransition = rememberInfiniteTransition(label = "warm_ptt_pulse")
    val pulseScale by if (isListening) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(550, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    // Warm Editorial States - NO BLUE, NO BLACK, NO GREEN, NO NEON GLOW
    val (bgBrush, ringColor, label, icon, iconColor, textColor) = when (voiceState) {
        VoiceState.IDLE -> Hexuple(
            Brush.linearGradient(listOf(WarmCard, WarmCardSubtle)),
            BorderWarm,
            "HOLD TO TALK",
            Icons.Default.Mic,
            WarmTerracotta,
            TextWarmPrimary
        )
        VoiceState.LISTENING -> Hexuple(
            Brush.linearGradient(listOf(WarmTerracotta, WarmTerracottaDark)),
            WarmTerracotta,
            "LISTENING...",
            Icons.Default.GraphicEq,
            Color.White,
            Color.White
        )
        VoiceState.PROCESSING -> Hexuple(
            Brush.linearGradient(listOf(WarmGold, WarmAmber)),
            WarmGold,
            "PROCESSING...",
            Icons.Default.Sync,
            Color.White,
            Color.White
        )
        VoiceState.SENDING -> Hexuple(
            Brush.linearGradient(listOf(WarmTerracottaDark, Color(0xFF8F3218))),
            BorderWarmActive,
            "TRANSMITTING...",
            Icons.AutoMirrored.Filled.Send,
            Color.White,
            Color.White
        )
        VoiceState.DELIVERED -> Hexuple(
            Brush.linearGradient(listOf(WarmAmber, Color(0xFF92400E))),
            WarmAmber,
            "DELIVERED",
            Icons.Default.Check,
            Color.White,
            Color.White
        )
        VoiceState.ERROR -> Hexuple(
            Brush.linearGradient(listOf(WarmCrimson, Color(0xFF991B1B))),
            WarmCrimson,
            "RETRY",
            Icons.Default.Mic,
            Color.White,
            Color.White
        )
    }

    val outerRingSize = buttonSize
    val secondaryRingSize = buttonSize * 0.88f
    val mainDialSize = buttonSize * 0.77f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(outerRingSize)
    ) {
        // Outer decorative warm border ring
        Box(
            modifier = Modifier
                .size(outerRingSize)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(if (isListening) WarmTerracottaLight else WarmCardSubtle)
                .border(
                    width = 1.dp,
                    color = if (isListening) WarmTerracotta.copy(alpha = 0.5f) else BorderWarm,
                    shape = CircleShape
                )
        )

        // Secondary warm ring
        Box(
            modifier = Modifier
                .size(secondaryRingSize)
                .clip(CircleShape)
                .background(if (isListening) WarmCardSelected else WarmCard)
                .border(1.dp, ringColor.copy(alpha = 0.5f), CircleShape)
        )

        // Main Warm Dial with crisp soft shadow (no neon glowing)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(mainDialSize)
                .shadow(
                    elevation = if (isListening) 8.dp else 3.dp,
                    shape = CircleShape,
                    spotColor = Color(0x33C7512E)
                )
                .clip(CircleShape)
                .background(bgBrush)
                .border(
                    width = if (isListening) 2.dp else 1.dp,
                    color = ringColor,
                    shape = CircleShape
                )
                .testTag("hold_to_talk_button")
                .pointerInput(isListening) {
                    detectTapGestures(
                        onTap = {
                            if (isListening) {
                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                onUp()
                            } else {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                onDown()
                            }
                        },
                        onPress = {
                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            onDown()
                            val released = tryAwaitRelease()
                            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                            onUp()
                        }
                    )
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Live Soundwave Bars if Listening
                if (isListening) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(20.dp)
                    ) {
                        val barWeights = listOf(0.4f, 0.7f, 1.0f, 0.8f, 0.5f)
                        barWeights.forEach { weight ->
                            val dynamicHeight = (6 + (amplitude * 20 * weight)).coerceIn(5f, 18f)
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(dynamicHeight.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = iconColor,
                        modifier = Modifier.size(if (buttonSize < 120.dp) 26.dp else 32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = label,
                    color = textColor,
                    fontSize = if (buttonSize < 120.dp) 10.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}

private data class Hexuple<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F
)



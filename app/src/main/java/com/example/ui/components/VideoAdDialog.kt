package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.DeepPurplePrimary
import com.example.ui.theme.GoldReward
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.ActiveAdState
import java.util.Locale

@Composable
fun VideoAdDialog(
    adState: ActiveAdState,
    onClaimReward: () -> Unit,
    onDismiss: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = {
            if (adState.isCompleted) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = adState.isCompleted,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("video_ad_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Sponsor & Close/Reward
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "বিজ্ঞাপন ভিডিও (Sponsored)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = adState.task.sponsorName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepPurplePrimary
                        )
                    }

                    if (adState.isCompleted) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("close_ad_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    } else {
                        // Countdown Ring
                        Surface(
                            shape = CircleShape,
                            color = DeepPurpleDark.copy(alpha = 0.1f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = {
                                        (adState.totalSeconds - adState.remainingSeconds).toFloat() /
                                                adState.totalSeconds.coerceAtLeast(1)
                                    },
                                    modifier = Modifier.size(36.dp),
                                    color = DeepPurplePrimary,
                                    strokeWidth = 3.dp,
                                )
                                Text(
                                    text = "${adState.remainingSeconds}s",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepPurpleDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Video Player Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF261C3B), Color(0xFF140D22))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(GoldReward),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (adState.isCompleted) Icons.Default.CardGiftcard else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = DeepPurpleDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (adState.isCompleted)
                                "বিজ্ঞাপন সম্পন্ন হয়েছে!"
                            else
                                "ভিডিও বিজ্ঞাপন চলছে...",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "পুরস্কার পেতে অপেক্ষা করুন: +${adState.task.rewardPoints} Pts",
                            color = GoldReward,
                            fontSize = 12.sp
                        )
                    }

                    // Sound Toggle inside player
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Audio Toggle",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Playback progress bar
                LinearProgressIndicator(
                    progress = {
                        (adState.totalSeconds - adState.remainingSeconds).toFloat() /
                                adState.totalSeconds.coerceAtLeast(1)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (adState.isCompleted) SuccessGreen else DeepPurplePrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action section
                if (adState.isCompleted) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "অভিনন্দন! আপনি বিজ্ঞাপনটি সম্পূর্ণ দেখেছেন।",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SuccessGreen,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onClaimReward,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .scale(pulseScale)
                                    .testTag("claim_ad_reward_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DeepPurpleDark,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = GoldReward,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ক্লাইম করুন (+${adState.task.rewardPoints} Points)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "পুরো বিজ্ঞাপনটি দেখলে ${adState.task.rewardPoints} পয়েন্ট যোগ হবে",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

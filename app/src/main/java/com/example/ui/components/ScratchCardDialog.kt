package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.GoldReward
import com.example.ui.theme.SuccessGreen

@Composable
fun ScratchCardDialog(
    onDismiss: () -> Unit,
    onRewardClaimed: (Int) -> Unit
) {
    val hiddenReward = remember { (40..90).random() }
    var scratchProgress by remember { mutableFloatStateOf(0f) }
    var isRevealed by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("scratch_card_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "লাকি স্ক্র্যাচ কার্ড",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepPurpleDark
                        )
                        Text(
                            text = "কার্ডটি ঘষে পুরস্কার বের করুন!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_scratch_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Scratch Card Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFF3D0), Color(0xFFFFE082))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Underneath revealed layer
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(GoldReward, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = DeepPurpleDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+$hiddenReward Points",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepPurpleDark
                        )
                        Text(
                            text = "অভিনন্দন!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    // Top scratch cover (scratchable surface)
                    if (!isRevealed) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            DeepPurpleDark.copy(alpha = (1f - scratchProgress * 0.8f).coerceAtLeast(0.1f)),
                                            Color(0xFF311B92).copy(alpha = (1f - scratchProgress * 0.8f).coerceAtLeast(0.1f))
                                        )
                                    )
                                )
                                .pointerInput(Unit) {
                                    detectDragGestures { _, dragAmount ->
                                        scratchProgress += (dragAmount.getDistance() / 800f)
                                        if (scratchProgress >= 0.75f) {
                                            isRevealed = true
                                        }
                                    }
                                }
                                .clickable {
                                    // Also allow tapping to scratch quickly
                                    scratchProgress += 0.35f
                                    if (scratchProgress >= 0.75f) {
                                        isRevealed = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = null,
                                    tint = GoldReward,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "এখানে স্ক্র্যাচ বা ট্যাপ করুন",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ঘষে বের করুন আপনার উপহার",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                AnimatedVisibility(
                    visible = isRevealed,
                    enter = fadeIn() + scaleIn()
                ) {
                    Button(
                        onClick = { onRewardClaimed(hiddenReward) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_scratch_reward_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text(
                            text = "ক্লাইম করুন (+$hiddenReward Points)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                if (!isRevealed) {
                    Text(
                        text = "কার্ডের উপর আঙুল দিয়ে ঘষুন",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

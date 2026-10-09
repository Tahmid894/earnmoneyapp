package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.DeepPurplePrimary
import com.example.ui.theme.GoldReward
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SpinWheelDialog(
    onDismiss: () -> Unit,
    onRewardClaimed: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var wonPoints by remember { mutableIntStateOf(0) }
    var hasWon by remember { mutableStateOf(false) }

    val segments = listOf(20, 50, 10, 100, 30, 75, 40, 60)
    val segmentColors = listOf(
        Color(0xFF673AB7),
        Color(0xFFFFB300),
        Color(0xFF3F51B5),
        Color(0xFFE91E63),
        Color(0xFF009688),
        Color(0xFFFF9800),
        Color(0xFF00BCD4),
        Color(0xFF9C27B0)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("spin_wheel_dialog"),
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
                            text = "ভাগ্যবান চাকা (Lucky Spin)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepPurpleDark
                        )
                        Text(
                            text = "চাকা ঘুরিয়ে নিশ্চিত পয়েন্ট জিতুন!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSpinning,
                        modifier = Modifier.testTag("close_spin_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wheel Container
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .size(230.dp)
                            .rotate(rotation.value)
                    ) {
                        val canvasSize = size.minDimension
                        val radius = canvasSize / 2
                        val center = Offset(radius, radius)
                        val anglePerSegment = 360f / segments.size

                        for (i in segments.indices) {
                            val startAngle = i * anglePerSegment
                            drawArc(
                                color = segmentColors[i % segmentColors.size],
                                startAngle = startAngle,
                                sweepAngle = anglePerSegment,
                                useCenter = true
                            )
                        }

                        // Inner circle
                        drawCircle(
                            color = Color.White,
                            radius = radius * 0.28f,
                            center = center
                        )
                        drawCircle(
                            color = DeepPurpleDark,
                            radius = radius * 0.22f,
                            center = center
                        )
                    }

                    // Indicator arrow on top
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(24.dp)
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val path = Path().apply {
                                moveTo(size.width / 2, size.height)
                                lineTo(0f, 0f)
                                lineTo(size.width, 0f)
                                close()
                            }
                            drawPath(path, color = Color(0xFFFF1744))
                        }
                    }

                    // Center Logo
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GoldReward, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SPIN",
                            color = DeepPurpleDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (hasWon) {
                    Text(
                        text = "অভিনন্দন! আপনি জিতেছেন $wonPoints পয়েন্ট!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onRewardClaimed(wonPoints) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_spin_reward_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Text("পয়েন্ট নিন (+ $wonPoints Pts)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                scope.launch {
                                    val winningIndex = Random.nextInt(segments.size)
                                    val selectedSegmentPts = segments[winningIndex]
                                    val anglePerSeg = 360f / segments.size
                                    val targetRotation = 360f * 5 + (360f - (winningIndex * anglePerSeg + anglePerSeg / 2))
                                    rotation.animateTo(
                                        targetValue = rotation.value + targetRotation,
                                        animationSpec = tween(durationMillis = 3200, easing = FastOutSlowInEasing)
                                    )
                                    wonPoints = selectedSegmentPts
                                    hasWon = true
                                    isSpinning = false
                                }
                            }
                        },
                        enabled = !isSpinning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("spin_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepPurpleDark,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = GoldReward
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSpinning) "চাকা ঘুরছে..." else "চাকা ঘোরান",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

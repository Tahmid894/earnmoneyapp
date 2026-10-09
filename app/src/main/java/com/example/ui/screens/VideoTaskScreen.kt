package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.TaskItem
import com.example.model.TaskType
import com.example.ui.components.DailyStreakCard
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.DeepPurplePrimary
import com.example.ui.theme.GoldReward
import com.example.ui.theme.SuccessGreen

@Composable
fun VideoTaskScreen(
    currentPoints: Int,
    streakDay: Int,
    canCheckIn: Boolean,
    tasks: List<TaskItem>,
    onTaskClick: (TaskItem) -> Unit,
    onDailyCheckInClick: () -> Unit,
    onOpenSpinWheel: () -> Unit,
    onOpenScratchCard: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("video_task_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Banner Art
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reward_banner),
                        contentDescription = "Reward Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay with readable text
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    )
                                )
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "প্রতিদিন ভিডিও দেখে আয় করুন",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "বিকাশ ও নগদে সরাসরি পেমেন্ট নিন",
                                    color = GoldReward,
                                    fontSize = 12.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DeepPurplePrimary.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = "১০ Pts = ১ BDT",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Check-in Streak Roadmap Card
        item {
            DailyStreakCard(
                currentStreakDay = streakDay,
                canCheckIn = canCheckIn,
                onCheckInClick = onDailyCheckInClick
            )
        }

        // Bonus Interactive Games (Lucky Spin & Scratch Card)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Spin Wheel Card
                Card(
                    onClick = onOpenSpinWheel,
                    modifier = Modifier
                        .weight(1f)
                        .height(95.dp)
                        .testTag("open_spin_wheel_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE91E63)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ভাগ্যবান চাকা",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepPurpleDark
                            )
                            Text(
                                text = "১০০ Pts পর্যন্ত",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scratch Card
                Card(
                    onClick = onOpenScratchCard,
                    modifier = Modifier
                        .weight(1f)
                        .height(95.dp)
                        .testTag("open_scratch_card_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00897B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "স্ক্র্যাচ কার্ড",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepPurpleDark
                            )
                            Text(
                                text = "ঘষে উপহার নিন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Matching Flutter code: Text('অ্যাড দেখে ইনকাম করুন', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.deepPurple))
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "অ্যাড দেখে ইনকাম করুন",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurpleDark
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = GoldReward.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = "${tasks.size}টি কাজ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepPurpleDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Tasks List
        items(tasks.size) { index ->
            val task = tasks[index]
            val icon = when (index) {
                0 -> Icons.Default.PlayCircle
                1 -> Icons.Default.VideoLibrary
                2 -> Icons.Default.CardGiftcard
                else -> Icons.Default.PlayCircle
            }
            val buttonText = when (task.type) {
                TaskType.DAILY_CHECKIN -> "ক্লাইম করুন"
                else -> "ভিডিও দেখুন"
            }

            TaskCard(
                task = task,
                icon = icon,
                buttonText = buttonText,
                onPressed = { onTaskClick(task) },
                testTag = "task_card_${task.id}"
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    icon: ImageVector,
    buttonText: String,
    onPressed: () -> Unit,
    testTag: String
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon matching Flutter: Icon(icon, size: 45, color: Colors.deepPurple)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(DeepPurplePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DeepPurpleDark,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Center details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Trailing Button matching Flutter: ElevatedButton(backgroundColor: Colors.deepPurple, child: Text(buttonText))
            Button(
                onClick = onPressed,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepPurpleDark,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier.testTag("action_${task.id}")
            ) {
                Text(
                    text = buttonText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

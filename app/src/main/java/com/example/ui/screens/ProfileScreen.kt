package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.WithdrawRequest
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.DeepPurplePrimary
import com.example.ui.theme.GoldReward
import com.example.ui.theme.SuccessGreen
import java.util.Locale

@Composable
fun ProfileScreen(
    currentPoints: Int,
    streakDay: Int,
    withdrawHistory: List<WithdrawRequest>,
    userProfile: UserProfile,
    onUpdateProfile: (String, String) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onResetData: () -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editEmail by remember { mutableStateOf(userProfile.email) }
    var showResetDialog by remember { mutableStateOf(false) }

    val totalWithdrawnBdt = remember(withdrawHistory) {
        withdrawHistory.sumOf { it.amountBdt }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Avatar Header matching Flutter:
        // CircleAvatar(radius: 50, backgroundColor: Colors.deepPurple, child: Icon(Icons.person, size: 60, color: Colors.white))
        // Text('Tahmid Sheikh', style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold))
        // Text('user@gmail.com', style: TextStyle(color: Colors.grey, fontSize: 16))
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(DeepPurpleDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userProfile.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                editName = userProfile.name
                                editEmail = userProfile.email
                                showEditDialog = true
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = DeepPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = userProfile.email,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stats Overview Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "বর্তমান পয়েন্ট",
                    value = "$currentPoints",
                    icon = Icons.Default.MonetizationOn,
                    tint = GoldReward,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "মোট উত্তোলন",
                    value = "৳${String.format(Locale.US, "%.0f", totalWithdrawnBdt)}",
                    icon = Icons.Default.Payments,
                    tint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "চেক-ইন স্ট্রিক",
                    value = "$streakDay দিন",
                    icon = Icons.Default.LocalFireDepartment,
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "রেফারেল",
                    value = "${userProfile.invitedFriends} জন",
                    icon = Icons.Default.CardGiftcard,
                    tint = DeepPurplePrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Referral Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("referral_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "বন্ধুদের রেফার করে আয় করুন!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DeepPurpleDark
                    )
                    Text(
                        text = "প্রতিটি সফল রেফারে আপনি পাবেন ১০০ পয়েন্ট বোনাস।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "আপনার রেফার কোড",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = userProfile.referralCode,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = DeepPurpleDark
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Referral Code", userProfile.referralCode)
                                    clipboard.setPrimaryClip(clip)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DeepPurplePrimary.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Code",
                                    tint = DeepPurpleDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Watch & Earn অ্যাপে জয়েন করুন এবং রেফার কোড ${userProfile.referralCode} ব্যবহার করে ফ্রি পয়েন্ট নিন!"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DeepPurplePrimary.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = DeepPurpleDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // How it works / FAQ Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = DeepPurpleDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "কীভাবে কাজ করে ও টাকা তুলবেন?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FaqItem(number = "১", text = "প্রতিটি ভিডিও বিজ্ঞাপন দেখে ৫০ থেকে ৭৫ পয়েন্ট আয় করুন।")
                    FaqItem(number = "২", text = "প্রতিদিন একবার ডেইলি চেক-ইন করে ১০০ পয়েন্ট বোনাস নিন।")
                    FaqItem(number = "৩", text = "১০ পয়েন্ট সমান ১ টাকা (BDT)। সর্বনিম্ন ১০ টাকা তোলা যাবে।")
                    FaqItem(number = "৪", text = "বিকাশ ও নগদে উইথড্র রিকোয়েস্ট পাঠালে ২৪ ঘণ্টার মধ্যে টাকা পাবেন।")
                }
            }
        }

        // Preferences & Reset
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "সেটিংস",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = DeepPurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "হ্যাপটিক ভাইব্রেশন", fontSize = 14.sp)
                        }
                        Switch(
                            checked = userProfile.vibrationEnabled,
                            onCheckedChange = { onToggleVibration(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = DeepPurpleDark)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = DeepPurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "সাউন্ড ইফেক্ট", fontSize = 14.sp)
                        }
                        Switch(
                            checked = userProfile.soundEnabled,
                            onCheckedChange = { onToggleSound(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = DeepPurpleDark)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_data_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "টেস্টিং ডাটা রিসেট করুন (২৫০ Pts)", color = Color.Gray)
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "প্রোফাইল সম্পাদন করুন", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("নাম") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("ইমেইল") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editEmail.isNotBlank()) {
                            onUpdateProfile(editName.trim(), editEmail.trim())
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurpleDark)
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Reset Data Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "ডাটা রিসেট করতে চান?", fontWeight = FontWeight.Bold) },
            text = {
                Text(text = "এটি আপনার পয়েন্ট ২৫০ তে ফিরিয়ে আনবে এবং সকল ইতিহাস মুছে ফেলবে।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurpleDark)
                ) {
                    Text("হ্যাঁ, রিসেট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FaqItem(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(DeepPurplePrimary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DeepPurpleDark
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}
